package com.example.epicture.ui.profile

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.epicture.R
import com.example.epicture.http.Album
import com.example.epicture.http.Gallery
import com.example.epicture.http.Image
import kotlinx.android.synthetic.main.favorite_list_view.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


class MyAdapterFavorite(
    private val context: Context,
    private val dataSource: List<Gallery>?,
    private val buttonCallback: (String?) -> Unit
) : RecyclerView.Adapter<MyAdapterFavorite.MyViewHolder>() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItemId(position: Int): Long {
        if (dataSource != null) {
            if (position >= dataSource.size)
                return -1
        }
        return position.toLong()
    }

    class MyViewHolder(val myView: View) : RecyclerView.ViewHolder(myView)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapterFavorite.MyViewHolder {
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
        if (dataSource != null)
        {
            Log.d("ImageDimension", holder.myView.buttonImageOrPreview.measuredHeight.toString())
            Log.d("ImageDimension", holder.myView.buttonImageOrPreview.measuredWidth.toString())
            Log.d("ImageDimension", dataSource[position].height.toString())
            Log.d("ImageDimension", dataSource[position].width.toString())
            holder.myView.usernameUploader.text = dataSource[position].account_url
            holder.myView.imageTitle.text = dataSource[position].title
            Glide.with(context).load(dataSource[position].cover).centerInside().into(holder.myView.buttonImageOrPreview)
            if (dataSource[position].is_album!!)
            {
                holder.myView.buttonImageOrPreview.setOnClickListener {
                    buttonCallback(dataSource[position].id)
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