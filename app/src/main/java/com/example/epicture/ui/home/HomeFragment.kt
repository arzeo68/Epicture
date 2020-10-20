package com.example.epicture.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epicture.App
import com.example.epicture.ImgurAuth
import com.example.epicture.R
import com.example.epicture.http.Image
import com.example.epicture.ui.profile.MyAdapterMyImage

class HomeFragment : Fragment() {

    private fun GetImagesResolve(data: List<Image>) {
        activity?.runOnUiThread {
            adapter = MyAdapterMyImage(requireContext(), data, {res,res2 ->likeCallback(res, res2)})
            _viewList?.adapter = adapter
        }
    }

    private fun likeCallback(id: String?, type: String)
    {

    }

    private var _viewList: RecyclerView? = null
    private lateinit var adapter: MyAdapterMyImage
    private var myUsername: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {

        super.onViewCreated(view, savedInstanceState)
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null)
        {
            myUsername = pseudo
            ImgurAuth.getImagesByAccountAuth({ res ->
                GetImagesResolve(res)
            }, { }, pseudo)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflate = inflater.inflate(R.layout.fragment_home, container, false)
        val viewManager = LinearLayoutManager(context)

        _viewList = inflate?.findViewById<RecyclerView>(R.id.imageList)?.apply {
            setHasFixedSize(false)
            adapter = MyAdapterMyImage(requireContext(), null, {res, res2 ->likeCallback(res, res2)})
            layoutManager = viewManager
        }

        return inflate
    }
}