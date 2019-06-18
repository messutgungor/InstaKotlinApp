package com.messutgungor.instakotlinapp.Home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment(){
    lateinit var fragmentView: View
    private val ACTIVITY_NUMBER = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_home,container,false)


        return fragmentView
    }


    override fun onResume() {
        setupNavigationView()
        super.onResume()
    }


    fun setupNavigationView(){

        var bottomNavigationView = fragmentView.bottomNavigationView
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomNavigationViewHelper.setupNavigation(activity!!,bottomNavigationView)

        //O an aktif olan aktivitenin ikonun seçili olması için
        var menu = bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NUMBER)
        menuItem.isChecked=true

    }
}