package com.resources.uploadlib.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.resources.uploadlib.gallery.PreviewFragment


/**
 * @Author:BYZ
 * @Time:2023/10/28 9:37
 * @blame Android Team
 * @info
 */
class GalleryAdapter(mManager: AppCompatActivity, var list:MutableList<PreviewFragment>) : FragmentStateAdapter(mManager) {

    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): PreviewFragment {
       return list[position]
    }


}