import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.databinding.ItemRvShortEditBinding
import com.toyou.toyouandroid.model.PreviewCardModel
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel

class ShortCardAdapter(private val cardViewModel: CardViewModel) : RecyclerView.Adapter<ShortCardAdapter.ViewHolder>() {

    private var cardList: List<PreviewCardModel> = emptyList()

    fun setCards(cards: List<PreviewCardModel>) {
        this.cardList = cards.filter { it.type == 0 }
        //notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRvShortEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cardList[position])
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    inner class ViewHolder(private val binding: ItemRvShortEditBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: PreviewCardModel) {
            binding.card = card
            binding.viewModel = cardViewModel

            // 기존의 answer 값을 EditText에 설정
            binding.memoEt.setText(card.answer)

            // EditText 텍스트 변경 리스너 설정
            binding.memoEt.doAfterTextChanged { text ->
                cardViewModel.updateCardAnswer(adapterPosition, text.toString())
            }

            binding.executePendingBindings()
        }
    }
}
