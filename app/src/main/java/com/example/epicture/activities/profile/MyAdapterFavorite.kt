package com.example.epicture.activities.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.epicture.R
import com.example.epicture.http.Gallery
import kotlinx.android.synthetic.main.favorite_list_view.view.*


class MyAdapterFavorite(
    private val context: Context,
    private val dataSource: List<Gallery>?,
    private val buttonCallback: (String?) -> Unit,
    private val buttonLikeCallback: (String?, String) -> Unit
) : RecyclerView.Adapter<MyAdapterFavorite.MyViewHolder>() {

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


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_list_view, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (dataSource != null) {
            holder.myView.usernameUploader.text = dataSource[position].account_url
            holder.myView.imageTitle.text = dataSource[position].title
            Glide.with(context).load(dataSource[position].cover)
                .into(holder.myView.buttonImageOrPreview)
            if (dataSource[position].is_album!!) {
                holder.myView.buttonImageOrPreview.setOnClickListener {
                    buttonCallback(dataSource[position].id)
                }
            }
            holder.myView.likeButtonInFavorite.setOnClickListener {
                var type = ""
                if (dataSource[position].is_album!!)
                    type = "album"
                else
                    type = "image"
                buttonLikeCallback(dataSource[position].id, type)
                if (holder.myView.likeButtonInFavorite.drawable.getConstantState()?.equals(
                        context.getResources().getDrawable(
                            R.drawable.ic_like_complete
                        ).getConstantState()
                    )!!
                )
                    holder.myView.likeButtonInFavorite.setImageResource(R.drawable.ic_unlike)
                else
                    holder.myView.likeButtonInFavorite.setImageResource(R.drawable.ic_like_complete)

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