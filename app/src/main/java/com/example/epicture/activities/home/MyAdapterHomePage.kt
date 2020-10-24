package com.example.epicture.activities.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.epicture.R
import com.example.epicture.services.http.HomeGallery
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.home_view_list.view.*
import kotlinx.android.synthetic.main.my_picture_view_list.view.imageTitle
import kotlinx.android.synthetic.main.my_picture_view_list.view.likeButton


class MyAdapterHomePage(
    private val context: Context,
    private val dataSource: MutableList<HomeGallery>?,
    private val buttonLikeCallback: (String?, String) -> Unit,
    private val clickOnAlbumCallback: (String?) -> Unit
) : RecyclerView.Adapter<MyAdapterHomePage.MyViewHolder>() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItemId(position: Int): Long {
        if (dataSource != null) {
            if (position >= dataSource.size)
                return -1
        }
        return position.toLong()
    }

    class MyViewHolder(val myView: View) : RecyclerView.ViewHolder(myView)


    // Create new views (invoked by the layout manager
    fun addItems(new: List<HomeGallery>)
    {
        if (dataSource != null) {
            dataSource.addAll(new)
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_view_list, parent, false)
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (dataSource != null) {
            holder.myView.imageTitle.text = dataSource[position].title
            Glide.with(context).load(dataSource[position].link).into(holder.myView.imageButton)
            if (dataSource[position].favorite!!)
                holder.myView.likeButton.setImageResource(R.drawable.ic_like_complete)
            holder.myView.likeButton.setOnClickListener {
                if (dataSource[position].is_album!!)
                    buttonLikeCallback(dataSource[position].id, "album")
                else
                    buttonLikeCallback(dataSource[position].id, "image")
                if (holder.myView.likeButton.drawable.getConstantState()?.equals(
                        context.getResources().getDrawable(
                            R.drawable.ic_like_complete
                        ).getConstantState()
                    )!!
                )
                    holder.myView.likeButton.setImageResource(R.drawable.ic_unlike)
                else
                    holder.myView.likeButton.setImageResource(R.drawable.ic_like_complete)

            }
            if (dataSource[position].is_album!!)
            {
                holder.myView.imageButton.setOnClickListener {
                    clickOnAlbumCallback(dataSource[position].id)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        if (dataSource != null) {
            return dataSource.size
        }
        return -1
    }
}