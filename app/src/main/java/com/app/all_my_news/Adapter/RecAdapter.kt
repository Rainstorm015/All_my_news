package com.app.all_my_news.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.all_my_news.Adapter.RecAdapter.RecViewHolder
import com.app.all_my_news.R
import com.app.all_my_news.Model.Section
import com.squareup.picasso.Picasso


class RecAdapter(private val list: List<Section>?) : RecyclerView.Adapter<RecViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecViewHolder {
        //Log.d("DebugTag", "hello")
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_section, parent, false)
        return RecViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecViewHolder, position: Int) {
        Log.d("DebugTag", "bind")
        var cpt: Int = 0
        for (section in list!!){
            holder.bind(section)
            cpt += 1
        }
        val section = list!![position]
        holder.bind(section)
        holder.itemView.setOnClickListener { v: View? ->
            section.expanded = !section.expanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    inner class RecViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.item_title)
        private val description: TextView = itemView.findViewById(R.id.sub_item_description)
        private val subItem: View = itemView.findViewById(R.id.sub_item)
        private val image: ImageView = itemView.findViewById(R.id.imageView)
        fun bind(section: Section) {
            //val recview : RecyclerView = itemView.rootView.findViewById(R.id.recview)!!
            /*for(section in recview){

            }*/
            //Log.d("Child count",""+ recview.childCount)
            subItem.visibility = if (section.expanded) View.VISIBLE else View.GONE
            title.text = section.title
            description.text = section.content
            Picasso.get().load(section.imgUrl).into(image)
        }
    }
}