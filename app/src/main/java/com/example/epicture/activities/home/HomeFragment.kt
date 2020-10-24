package com.example.epicture.activities.home

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.epicture.App
import com.example.epicture.ImgurAuth
import com.example.epicture.R
import com.example.epicture.http.AlbumImage
import com.example.epicture.http.HomeGallery
import com.example.epicture.ui.profile.MyAdapterInAlbum
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private fun GetImagesResolve(data: List<HomeGallery>) {

        activity?.runOnUiThread {
            var sdfsdf: MutableList<HomeGallery> = data.toMutableList()
            adapter =
                MyAdapterHomePage(
                    requireContext(),
                    sdfsdf,
                    { res, res2 -> likeCallback(res, res2) },
                    { res ->
                        clickOnAlbumCallback(
                            res
                        )
                    })
            _viewList?.adapter = adapter
        }
    }

    private fun GetNextPageResolve(data: List<HomeGallery>) {

        activity?.runOnUiThread {
            loadingFinished = true
            myRefreshLayout.isRefreshing = false
            var sdfsdf: MutableList<HomeGallery> = data.toMutableList()
            adapter.addItems(data)
            adapter.notifyDataSetChanged()
        }
    }


    private fun likeCallback(id: String?, type: String) {
        if (id != null) {
            ImgurAuth.putFavorite({ likeResolve() }, {}, id, type)
        }
    }

    private fun likeResolve() {

    }

    private fun switchMode() {
        _page = 0
        if (searchMode) {
            initSortListSearch()
            getSearchImage()
        } else {
            initSortList()
            getHomeImage()
        }
    }

    private fun getHomeImage() {
        myRefreshLayout.isRefreshing = true
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context).getString(
            "account_username",
            ""
        )
        if (pseudo != null) {
            myUsername = pseudo
            ImgurAuth.getGallery(
                { res ->
                    isInALubum  = false
                    sortButton.setImageResource(R.drawable.ic_filter_list)
                    GetImagesResolve(res)
                    myRefreshLayout.isRefreshing = false
                },
                {myRefreshLayout.isRefreshing = false },
                "0",
                _homeParam[0].toLowerCase(),
                _homeParam[1].toLowerCase(),
                _homeParam[2].toLowerCase()
            )
        }
    }

    private fun getSearchImage() {
        if (searchText == "")
            return
        myRefreshLayout.isRefreshing = true
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context).getString(
            "account_username",
            ""
        )
        if (pseudo != null) {
            Log.d("ertyuiop", _searchParam[0])
            Log.d("ertyuiop", _searchParam[1])
            Log.d("ertyuiop", "END")

            myUsername = pseudo
            ImgurAuth.searchGallery(
                { res ->
                    isInALubum  = false
                    sortButton.setImageResource(R.drawable.ic_filter_list)
                    GetImagesResolve(res)
                    myRefreshLayout.isRefreshing = false
                },
                {myRefreshLayout.isRefreshing = false },
                searchText,
                "0", _searchParam[0], _searchParam[1]
            )
        }
    }

    private fun getNextPage() {

        if (!searchMode) {
            val pseudo: String? =
                PreferenceManager.getDefaultSharedPreferences(App.context).getString(
                    "account_username",
                    ""
                )
            if (pseudo != null) {
                Log.d("FGHJKKJHDGHJ", "call start")
                myUsername = pseudo
                myRefreshLayout.isRefreshing = true
                ImgurAuth.getGallery(
                    { res ->
                        isInALubum  = false
                        sortButton.setImageResource(R.drawable.ic_filter_list)
                        GetNextPageResolve(res)
                        myRefreshLayout.isRefreshing = false
                    },
                    { myRefreshLayout.isRefreshing = false },
                    _page.toString(),
                    _homeParam[0].toLowerCase(),
                    _homeParam[1].toLowerCase(),
                    _homeParam[2].toLowerCase()
                )
            }
        } else {
            if (searchText == "")
                return
            val pseudo: String? =
                PreferenceManager.getDefaultSharedPreferences(App.context).getString(
                    "account_username",
                    ""
                )
            if (pseudo != null) {
                myRefreshLayout.isRefreshing = true
                Log.d("FGHJKKJHDGHJ", "call start")
                myUsername = pseudo
                ImgurAuth.searchGallery(
                    { res ->
                        isInALubum  = false
                        sortButton.setImageResource(R.drawable.ic_filter_list)
                        GetNextPageResolve(res)
                        myRefreshLayout.isRefreshing = false
                    },
                    { myRefreshLayout.isRefreshing = false},
                    searchText,
                    _page.toString(), _searchParam[0], _searchParam[1]
                )
            }
        }
    }
    private var isInALubum = false
    private var searchText: String = ""
    private var searchMode = false
    private var loadingFinished = true
    private var _page = 0
    private lateinit var _homeParam: ArrayList<String>
    private lateinit var _searchParam: ArrayList<String>
    private var _viewList: RecyclerView? = null
    private lateinit var adapter: MyAdapterHomePage
    private var myUsername: String = ""
    private lateinit var _dialog: Dialog
    private lateinit var _sort1: RadioGroup
    private lateinit var _sort2: RadioGroup
    private lateinit var _sort3: RadioGroup
    private lateinit var _sort4: RadioGroup
    private lateinit var _sort5: RadioGroup
    private lateinit var adapterInAlbum: MyAdapterInAlbum
    private lateinit var myRefreshLayout: SwipeRefreshLayout


    fun Fragment.hideKeyboard() {
        val view = activity?.currentFocus
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView = requireActivity().currentFocus
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
        }
    }

    fun clickOnAlbumCallback(data: String?) {

        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")
        if (pseudo != null) {
            if (data != null) {
                ImgurAuth.getAlbumImages(
                    { res ->
                        getImagesInAlbumResolve(res)
                        isInALubum = true
                        sortButton.setImageResource(R.drawable.ic_left_arrow)
                    },
                    {
                        Log.d("JHGFDDF", "call back failed")
                    }, data
                )
            }
        }
    }

    private fun getImagesInAlbumResolve(data: List<AlbumImage>) {
        activity?.runOnUiThread {
            Log.d("JHGFDDF", "call back resolve")
            adapterInAlbum = MyAdapterInAlbum(requireContext(), data)
            _viewList?.adapter = adapterInAlbum
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        myRefreshLayout = refreshLayout1
        initSortListSearch()
        initSortList()
        val pseudo: String? = PreferenceManager.getDefaultSharedPreferences(App.context)
            .getString("account_username", "")

        myRefreshLayout.setOnRefreshListener {
            switchMode()
            myRefreshLayout.isRefreshing = false
        }
        sortButton.setOnClickListener {
            if (isInALubum)
            {
                switchMode()
            }
            else
                _dialog.show()
        }

        searchButton.setOnClickListener {
            hideKeyboard()
            searchText = searchBar.text.toString()
            searchBar.clearFocus()
            searchMode = true
            if (searchText != "")
                switchMode()
        }
        cancelButton.setOnClickListener {
            hideKeyboard()
            searchText = ""
            searchBar.text?.clear()
            searchBar.clearFocus()
            searchMode = false
            switchMode()
        }

        _viewList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && loadingFinished) {
                    loadingFinished = false
                    _page += 1
                    getNextPage()
                }
            }
        })

        getHomeImage()


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
            layoutManager = viewManager
        }

        return inflate
    }

    private fun initSortListSearch() {

        // custom dialog
        _dialog = context?.let { Dialog(it) }!!
        if (_dialog != null) {
            _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            _dialog.setContentView(R.layout.sort_chooser_search)
        }
        _searchParam = ArrayList()
        _searchParam.add("time")
        _searchParam.add("all")
        _sort4 = _dialog?.findViewById(R.id.sort1) as RadioGroup
        _sort4.check(_sort4.getChildAt(1).id)
        _sort5 = _dialog?.findViewById(R.id.sort2) as RadioGroup
        _sort5.check(_sort5.getChildAt(5).id)

        _sort4.setOnCheckedChangeListener { group, checkedId ->
            val childCount = group.childCount
            for (x in 1 until childCount) {
                val btn = group.getChildAt(x) as RadioButton
                if (btn.id == checkedId) {
                    Log.d("MANGETESMORTS", btn.text.toString())
                    _searchParam[0] = btn.text.toString()
                    getSearchImage()
                }
            }
        }
        _sort5.setOnCheckedChangeListener { group, checkedId ->
            val childCount = group.childCount
            for (x in 1 until childCount) {
                val btn = group.getChildAt(x) as RadioButton
                if (btn.id == checkedId) {
                    Log.d("MANGETESMORTS", btn.text.toString())
                    _searchParam[1] = btn.text.toString()
                    getSearchImage()

                }
            }
        }
    }

    private fun initSortList() {

        // custom dialog
        _dialog = context?.let { Dialog(it) }!!
        if (_dialog != null) {
            _dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            _dialog.setContentView(R.layout.sort_chooser_home)
        }
        _homeParam = ArrayList()
        _homeParam.add("Hot")
        _homeParam.add("Viral")
        _homeParam.add("Day")
        _sort1 = _dialog?.findViewById(R.id.sort1) as RadioGroup
        _sort1.check(_sort1.getChildAt(1).id)
        _sort2 = _dialog?.findViewById(R.id.sort2) as RadioGroup
        _sort2.check(_sort2.getChildAt(1).id)
        _sort3 = _dialog?.findViewById(R.id.sort3) as RadioGroup
        _sort3.check(_sort3.getChildAt(1).id)

        _sort1.setOnCheckedChangeListener { group, checkedId ->
            val childCount = group.childCount
            for (x in 1 until childCount) {
                val btn = group.getChildAt(x) as RadioButton
                if (btn.id == checkedId) {
                    Log.d("MANGETESMORTS", btn.text.toString())
                    _homeParam[0] = btn.text.toString()
                    if (!searchMode)
                        getHomeImage()
                    else
                        getSearchImage()
                }
            }
        }

        _sort2.setOnCheckedChangeListener { group, checkedId ->
            val childCount = group.childCount
            for (x in 1 until childCount) {
                val btn = group.getChildAt(x) as RadioButton
                if (btn.id == checkedId) {
                    Log.d("MANGETESMORTS", btn.text.toString())
                    _homeParam[1] = btn.text.toString()
                    if (!searchMode)
                        getHomeImage()
                    else
                        getSearchImage()

                }
            }
        }

        _sort3.setOnCheckedChangeListener { group, checkedId ->
            val childCount = group.childCount
            for (x in 1 until childCount) {
                val btn = group.getChildAt(x) as RadioButton
                if (btn.id == checkedId) {
                    Log.d("MANGETESMORTS", btn.text.toString())
                    _homeParam[2] = btn.text.toString()
                    if (!searchMode)
                        getHomeImage()
                    else
                        getSearchImage()
                }
            }
        }

        _viewList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && loadingFinished) {
                    loadingFinished = false
                    myRefreshLayout.isRefreshing = true
                    _page += 1
                    getNextPage()
                }
            }
        })

        getHomeImage()


    }
}