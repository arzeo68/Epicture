package com.example.epicture.activities.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.epicture.R
import com.example.epicture.http.Image
import kotlinx.android.synthetic.main.my_picture_view_list.view.*


class MyAdapterMyImage(
    private val context: Context,
    private val dataSource: List<Image>?,
    private val buttonDeleteCallback: (String?, String) -> Unit
) : RecyclerView.Adapter<MyAdapterMyImage.MyViewHolder>() {

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
            .inflate(R.layout.my_picture_view_list, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (dataSource != null) {
            holder.myView.imageTitle.text = dataSource[position].name
            holder.myView.description_image.text = dataSource[position].description
            Glide.with(context).load(dataSource[position].link).into(holder.myView.image)
            if (dataSource[position].favorite!!)
                holder.myView.likeButton.setImageResource(R.drawable.ic_like_complete)
            holder.myView.likeButton.setOnClickListener {

                buttonDeleteCallback(dataSource[position].id, "image")

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