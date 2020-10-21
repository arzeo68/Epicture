package com.example.epicture.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epicture.App
import com.example.epicture.ImgurAuth
import com.example.epicture.R
import com.example.epicture.http.Image
import com.example.epicture.ui.profile.MyAdapterMyImage
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private fun GetImagesResolve(data: List<Image>) {
        activity?.runOnUiThread {
            adapter =
                MyAdapterHomePage(requireContext(), data, { res, res2 -> likeCallback(res, res2) })
            _viewList?.adapter = adapter
        }
    }

    private fun likeCallback(id: String?, type: String) {

    }

    private var _viewList: RecyclerView? = null
    private lateinit var adapter: MyAdapterHomePage
    private var myUsername: String = ""

    fun Fragment.hideKeyboard() {
        val view = activity?.currentFocus
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")

        searchButton.setOnClickListener {
            // when user press on search button
        }
        cancelButton.setOnClickListener {
            hideKeyboard()
            searchBar.text?.clear()
            searchBar.clearFocus()
        }

        if (pseudo != null) {
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
            adapter =
                MyAdapterMyImage(requireContext(), null, { res, res2 -> likeCallback(res, res2) })
            layoutManager = viewManager
        }

        return inflate
    }
}