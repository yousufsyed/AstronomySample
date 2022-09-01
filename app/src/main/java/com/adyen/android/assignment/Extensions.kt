package com.adyen.android.assignment

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.adyen.android.assignment.data.AstronomyInfo
import com.adyen.android.assignment.di.AppComponent
import com.adyen.android.assignment.di.AppComponentNotFoundException
import com.squareup.picasso.Picasso

// Inflater Extension
val ViewGroup.inflater: LayoutInflater
    get() = LayoutInflater.from(context)

// Picasso
fun ImageView.load(url: String?) {
    Picasso
        .with(this.context)
        .load(
            if (url.isNullOrEmpty()) {
                null
            } else {
                url
            }
        )
        .placeholder(R.drawable.ic_placeholder)
        .into(this)
}

// App component Extension
val Fragment.component: AppComponent
    get() = run {
        (requireActivity().application as? AdyenApplication)?.appComponent
            ?: throw AppComponentNotFoundException()
    }

val Activity.component: AppComponent
    get() = run {
        (application as? AdyenApplication)?.appComponent
            ?: throw AppComponentNotFoundException()
    }

fun List<AstronomyInfo.PlanetaryData>.sortByComparator(comparator: Comparator<AstronomyInfo.PlanetaryData>?) =
    comparator?.let { this.sortedWith(it) } ?: this