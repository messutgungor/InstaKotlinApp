package com.messutgungor.instakotlinapp.Share


import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.messutgungor.instakotlinapp.R
import kotlinx.android.synthetic.main.fragment_yukleniyor.view.*


class CompressLoadingFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view = inflater.inflate(R.layout.fragment_compress_loading, container, false)

        view.progressBarYuklaniyor.indeterminateDrawable.setColorFilter(ContextCompat.getColor(activity!!,R.color.siyah),PorterDuff.Mode.SRC_IN)

        return view
    }


}
