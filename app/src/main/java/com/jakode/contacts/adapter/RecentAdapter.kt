package com.jakode.contacts.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jakode.contacts.R
import com.jakode.contacts.data.model.Recent
import com.jakode.contacts.utils.ImageSetter
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.recent_list_item.view.*

class RecentAdapter(private val list: ArrayList<Recent>) :
    RecyclerView.Adapter<RecentAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.recent_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Init
        holder.setData(list[position])

        // OnClick listener
        onClickListener(holder)
    }

    private fun onClickListener(holder: ViewHolder) {
        holder.view.setOnClickListener(holder)
        holder.more.setOnClickListener(holder)
        holder.call.setOnClickListener(holder)
        holder.massage.setOnClickListener(holder)
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val view: ConstraintLayout by lazy { itemView.recent_view }
        val cover: RoundedImageView by lazy { itemView.recent_cover }

        val more: ImageView by lazy { itemView.recent_more }
        val call: ImageView by lazy { itemView.recent_call }
        val massage: ImageView by lazy { itemView.recent_massage }

        val firstName: TextView by lazy { itemView.recent_first_name }
        val lastName: TextView by lazy { itemView.recent_last_name }
        val recentTime: TextView by lazy { itemView.recent_time }

        fun setData(recent: Recent) {
            ImageSetter.set(recent.user.cover, cover)
            firstName.text = recent.user.firstName
            lastName.text = recent.user.lastName
            recentTime.text = recent.recentTime
        }

        override fun onClick(view: View?) {
            when (view?.id) {
                R.id.recent_more -> {
                    Toast.makeText(context, "more", Toast.LENGTH_SHORT).show()
                }
                R.id.recent_call -> {
                    Toast.makeText(context, "call", Toast.LENGTH_SHORT).show()
                }
                R.id.recent_massage -> {
                    Toast.makeText(context, "massage", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "body", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}