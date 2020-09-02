package pl.redny.kagouti.presentation.ui.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.view.*
import android.webkit.CookieManager
import android.webkit.MimeTypeMap
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import pl.redny.kagouti.BuildConfig
import pl.redny.kagouti.R
import pl.redny.kagouti.application.download.DownloadFile
import pl.redny.kagouti.databinding.MainFragmentBinding
import pl.redny.kagouti.domain.DownloadResult
import pl.redny.kagouti.presentation.component.viewer.FileViewer
import java.io.File


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val PERMISSIONS =
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val PERMISSION_REQUEST_CODE = 1
    private val DOWNLOAD_FILE_CODE = 2

    private val fileUrl =
        "https://d2v9y0dukr6mq2.cloudfront.net/video/thumbnail/rcxbst_b0itvu9rs2/kitten-in-a-cup-turns-its-head-and-watches_raeb_02je_thumbnail-full01.png"

    private val downloadFile : DownloadFile by inject()
    private val fileViewer: FileViewer by inject()

    private lateinit var binding: MainFragmentBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var urlRegex: Regex

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.main_fragment,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        checkPermissions()

        configWebview()

        urlRegex = Regex("^https://embed.gog.com/on_login_success.+")
    }

    private fun configWebview() {
        webview.webViewClient = object : WebViewClient() {
            private fun getRedirectUrl(url: String?): String? {
                if (url?.contains(urlRegex) == true) {
                    return "https://gog.com"
                }
                return url
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(getRedirectUrl(url))

                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                val cookies: String? = CookieManager.getInstance().getCookie(url)
                Toast.makeText(activity, cookies, Toast.LENGTH_LONG).show()
            }
        }
        webview.settings.domStorageEnabled = true
        webview.settings.javaScriptEnabled = true
        webview.loadUrl("https://auth.gog.com/auth?client_id=46899977096215655&redirect_uri=https%3A%2F%2Fembed.gog.com%2Fon_login_success%3Forigin%3Dclient&response_type=code&layout=client2")
    }

    private fun checkPermissions() {
        if (hasPermissions(context, PERMISSIONS)) {
            setDownloadButtonClickListener()
        } else {
            requestPermissions(PERMISSIONS.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    private fun hasPermissions(context: Context?, permissions: List<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            return permissions.all { permission ->
                ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE && hasPermissions(context, PERMISSIONS)) {
            setDownloadButtonClickListener()
        }
    }

    private fun setDownloadButtonClickListener() {
        val folder = context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val fileName = "kitten_in_a_cup.png"
        val file = File(folder, fileName)
        val uri = context?.let {
            FileProvider.getUriForFile(it, "${BuildConfig.APPLICATION_ID}.provider", file)
        }
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri?.path)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

        binding.viewButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.setDataAndType(uri, mimeType)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra(Intent.EXTRA_TITLE, fileName)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, DOWNLOAD_FILE_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DOWNLOAD_FILE_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                context?.let { context ->
                    downloadFile(context, fileUrl, uri)
                }
            }
        }
    }

    private fun downloadFile(context: Context, url: String, file: Uri) {
        viewModel.setDownloading(true)
        context.contentResolver.openOutputStream(file)?.let { outputStream ->
            CoroutineScope(Dispatchers.IO).launch {
                downloadFile.downloadFile(outputStream, url).collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is DownloadResult.Success -> {
                                viewModel.setDownloading(false)
                                binding.progressBar.progress = 0
                                fileViewer.viewFile(this@MainFragment.requireActivity(), file)
                            }

                            is DownloadResult.Error -> {
                                viewModel.setDownloading(false)
                                Toast.makeText(
                                    context,
                                    "Error while downloading file",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            is DownloadResult.Progress -> {
                                binding.progressBar.progress = it.progress
                            }
                        }
                    }
                }
            }
        }
    }

}
