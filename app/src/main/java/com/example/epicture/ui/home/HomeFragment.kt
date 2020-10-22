package com.example.epicture.ui.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.RadioGroup
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
    private lateinit var _dialog: Dialog
    private lateinit var _sort1: RadioGroup
    private lateinit var _sort2: RadioGroup
    private lateinit var _sort3: RadioGroup

    fun Fragment.hideKeyboard() {
        val view = activity?.currentFocus
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")

        sortButton.setOnClickListener {
            _dialog.show()
        }

        searchButton.setOnClickListener {
            // when user press on search button
        }
        cancelButton.setOnClickListener {
            hideKeyboard()
            searchBar.text?.clear()
            searchBar.clearFocus()
        }

        _sort1.setOnCheckedChangeListener { group, checkedId ->
            val childCount = group.childCount
            for (x in 0 until childCount) {
                val btn = group.getChildAt(x) as RadioButton
                if (btn.id == checkedId) {
                    Log.d("MANGETESMORTS", btn.text.toString())
                }
            }
        }

        _sort2.setOnCheckedChangeListener { group, checkedId ->
            val childCount = group.childCount
            for (x in 0 until childCount) {
                val btn = group.getChildAt(x) as RadioButton
                if (btn.id == checkedId) {
                    Log.d("MANGETESMORTS", btn.text.toString())
                }
            }
        }

        _sort3.setOnCheckedChangeListener { group, checkedId ->
            val childCount = group.childCount
            for (x in 0 until childCount) {
                val btn = group.getChildAt(x) as RadioButton
                if (btn.id == checkedId) {
                    Log.d("MANGETESMORTS", btn.text.toString())
                }
            }
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
        initSortList()
        _viewList = inflate?.findViewById<RecyclerView>(R.id.imageList)?.apply {
            setHasFixedSize(false)
            adapter =
                MyAdapterMyImage(requireContext(), null, { res, res2 -> likeCallback(res, res2) })
            layoutManager = viewManager
        }

        return inflate
    }

    private fun initSortList() {

        // custom dialog
        _dialog = context?.let { Dialog(it) }!!
        if (_dialog != null) {
            _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            _dialog.setContentView(R.layout.sort_chooser)
        }
        _sort1 = _dialog?.findViewById(R.id.sort1) as RadioGroup
        _sort1.check(_sort1.getChildAt(0).id)
        _sort2 = _dialog?.findViewById(R.id.sort2) as RadioGroup
        _sort2.check(_sort2.getChildAt(0).id)
        _sort3 = _dialog?.findViewById(R.id.sort3) as RadioGroup
        _sort3.check(_sort3.getChildAt(0).id)
    }
}