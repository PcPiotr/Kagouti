package pl.redny.kagouti.presentation.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.main_fragment.*
import pl.redny.kagouti.R


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var urlRegex: Regex

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        webview.webViewClient = object : WebViewClient() {
            private fun getRedirectUrl(url: String?): String? {
                if(url?.contains(urlRegex) == true) {
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
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        urlRegex = Regex("^https://embed.gog.com/on_login_success.+")
        // TODO: Use the ViewModel
    }

}
