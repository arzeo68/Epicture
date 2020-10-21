package com.example.epicture.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.epicture.R
import com.example.epicture.http.AlbumImage
import kotlinx.android.synthetic.main.image_view_on_profile_page_in_album.view.*


class MyAdapterInAlbum(
    private val context: Context,
    private val dataSource: List<AlbumImage>?
) : RecyclerView.Adapter<MyAdapterInAlbum.MyViewHolder>() {

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
            .inflate(R.layout.image_view_on_profile_page_in_album, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (dataSource != null) {
            Glide.with(context).load(dataSource[position].link).into(holder.myView.image);
        }

    }

    override fun getItemCount(): Int {
        if (dataSource != null) {
            return dataSource.size
        }
        return -1
    }


}