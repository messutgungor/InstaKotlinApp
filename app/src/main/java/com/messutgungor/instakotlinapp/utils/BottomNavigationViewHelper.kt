package com.messutgungor.instakotlinapp.utils

import android.content.Context
import android.content.Intent
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.messutgungor.instakotlinapp.Home.HomeActivity
import com.messutgungor.instakotlinapp.News.NewsActivity
import com.messutgungor.instakotlinapp.Profile.ProfileActivity
import com.messutgungor.instakotlinapp.R
import com.messutgungor.instakotlinapp.Search.SearchActivity
import com.messutgungor.instakotlinapp.Share.ShareActivity

class BottomNavigationViewHelper {

    companion object{
        //Burası Java da static nesnelere denk geliyor. yani bir nesne oluşturmadan başka bir sınıftan direk ulaşabiliyoruz.

        fun setupBottomNavigationView(bottomNavigationViewEx: BottomNavigationViewEx){
            bottomNavigationViewEx.enableAnimation(false)
            //bottomNavigationViewEx.enableItemShiftingMode(false)
            //bottomNavigationViewEx.enableShiftingMode(false)
            bottomNavigationViewEx.labelVisibilityMode=1
            bottomNavigationViewEx.isItemHorizontalTranslationEnabled=false
            bottomNavigationViewEx.setTextVisibility(false)
        }

        fun setupNavigation(context: Context,bottomNavigationViewEx: BottomNavigationViewEx){
            bottomNavigationViewEx.onNavigationItemSelectedListener=object : BottomNavigationView.OnNavigationItemSelectedListener{
                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                       when(item.itemId){
                           R.id.ic_home -> {
                               val intent = Intent(context,HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                               context.startActivity(intent)
                               return true
                           }
                           R.id.ic_search ->{
                               val intent = Intent(context,SearchActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                               context.startActivity(intent)
                               return true

                           }
                           R.id.ic_share ->{
                               val intent = Intent(context,ShareActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                               context.startActivity(intent)
                               return true

                           }
                           R.id.ic_news ->{
                               val intent = Intent(context,NewsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                               context.startActivity(intent)
                               return true

                           }
                           R.id.ic_profile ->{
                               val intent = Intent(context,ProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                               context.startActivity(intent)
                               return true

                           }

                       }
                    return false
                }
            }
        }


    }
}