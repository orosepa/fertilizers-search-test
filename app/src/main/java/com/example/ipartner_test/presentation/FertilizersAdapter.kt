package com.example.ipartner_test.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ipartner_test.R
import com.example.ipartner_test.databinding.ItemFertilizersBinding
import com.example.ipartner_test.domain.models.IPartnerResponse
import com.example.ipartner_test.util.Constants

class FertilizersAdapter : RecyclerView.Adapter<FertilizersAdapter.ResponseViewHolder>() {

    inner class ResponseViewHolder(private val binding: ItemFertilizersBinding):
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<IPartnerResponse>() {
        override fun areItemsTheSame(oldItem: IPartnerResponse, newItem: IPartnerResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: IPartnerResponse, newItem: IPartnerResponse): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseViewHolder {
        return ResponseViewHolder(
            ItemFertilizersBinding.bind(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_fertilizers,
                    parent,
                    false
                )
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((IPartnerResponse) -> Unit)? = null

    override fun onBindViewHolder(holder: ResponseViewHolder, position: Int) {
        val fertilizer = differ.currentList[position]
        holder.itemView.apply {
            val image: ImageView = findViewById(R.id.fertilizer_image)
            val icon: ImageView = findViewById(R.id.category_icon)
            val title: TextView = findViewById(R.id.fertilizer_title)
            val description: TextView = findViewById(R.id.fertilizer_description)

            Glide.with(this).load(Constants.BASE_URL + fertilizer.image).into(image)
            Glide.with(this).load(Constants.BASE_URL + fertilizer.categories.icon).into(icon)

            title.text = fertilizer.name
            description.text = fertilizer.description

            setOnClickListener {
                onItemClickListener?.let { it(fertilizer) }
            }
        }
    }

    fun setOnItemClickListener(listener: (IPartnerResponse) -> Unit) {
        onItemClickListener = listener
    }

}