package com.hsdroid.newsarticle.ui.view.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hsdroid.newsarticle.databinding.BottomsheetWebviewBinding
import com.hsdroid.newsarticle.utils.Helper.Companion.copyToClipboard

class WebViewBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomsheetWebviewBinding
    private lateinit var webView: WebView
    private lateinit var urlToArticle: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let {
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        urlToArticle = arguments?.getString("url") ?: ""

        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {

        webView = binding.webView
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.tvTitle.text = view?.title
                binding.tvUrl.text = view?.url

                binding.progressCircular.visibility = View.GONE
                binding.imgCopy.visibility = View.VISIBLE
                binding.webView.visibility = View.VISIBLE
            }
        }
        webView.loadUrl(urlToArticle)
        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)

        binding.imgClose.setOnClickListener {
            dismiss()
        }

        binding.imgCopy.setOnClickListener {
            copyToClipboard(requireContext(), urlToArticle)
        }
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }
}
