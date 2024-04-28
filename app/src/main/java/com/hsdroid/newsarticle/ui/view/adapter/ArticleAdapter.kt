package com.hsdroid.newsarticle.ui.view.adapter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hsdroid.newsarticle.R
import com.hsdroid.newsarticle.data.models.Article
import com.hsdroid.newsarticle.databinding.ItemArticleBinding
import com.hsdroid.newsarticle.ui.view.activity.MainActivity
import com.hsdroid.newsarticle.ui.view.bottomsheet.WebViewBottomSheet
import com.hsdroid.newsarticle.utils.Helper.Companion.formatDateTime

class ArticleAdapter(val mainActivity: MainActivity) :
    RecyclerView.Adapter<ArticleAdapter.MyViewHolder>() {

    private var list: List<Article>? = null

    inner class MyViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            with(binding) {
                Glide.with(mainActivity).load(article.urlToImage)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressCircular.visibility = View.GONE
                            binding.imgArticle.apply {
                                visibility = View.VISIBLE
                                setImageDrawable(
                                    ContextCompat.getDrawable(
                                        itemView.context,
                                        R.drawable.baseline_error_24
                                    )
                                )
                            }

                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressCircular.visibility = View.GONE
                            binding.imgArticle.visibility = View.VISIBLE
                            return false
                        }

                    }).into(imgArticle)

                tvTitle.text = article.title
                tvSource.text = article.source.name
                tvPublishedAt.text = formatDateTime(article.publishedAt)

                itemView.setOnClickListener {
                    val bottomSheet = WebViewBottomSheet().apply {
                        arguments = Bundle().apply {
                            putString("url", article.url)
                        }
                    }
                    bottomSheet.show(mainActivity.supportFragmentManager, bottomSheet.tag)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        if (list?.size == null) return 0
        return list!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list!![position])
    }

    fun setList(list: List<Article>) {
        this.list = list
        notifyDataSetChanged()
    }
}