package com.messutgungor.instakotlinapp.Profile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile_edit.view.*


class ProfileEditFragment : Fragment() {

    lateinit var circleProfileImage:CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view =  inflater.inflate(R.layout.fragment_profile_edit, container, false)

        circleProfileImage=view.findViewById(R.id.circleProfilimage)
        initImageLoader()
        setupProfilePicture()

        view.imgClose.setOnClickListener {
            activity!!.onBackPressed()
        }

        return view
    }

    private fun initImageLoader(){
        var universalImageLoader=UniversalImageLoader(activity!!)
        ImageLoader.getInstance().init(universalImageLoader.config)

    }

    private fun setupProfilePicture() {

        //http://mehmetusak.com/wp-content/uploads/2018/10/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg
        var imgURL="mehmetusak.com/wp-content/uploads/2018/10/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg"

        UniversalImageLoader.setImage(imgURL,circleProfileImage,null,"http://")
    }

}