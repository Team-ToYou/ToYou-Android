import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
        notifyDataSetChanged()
        this.cardList = cards.filter { it.type == 0 }
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

            binding.memoEt.setText(card.answer)

            binding.memoEt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    card.answer = s.toString()  // 변경된 텍스트를 PreviewCardModel의 answer에 저장
                    binding.limit200.text = String.format("(%d/50)", s?.length ?: 0)  // 글자 수 업데이트

                    val isNotEmpty = !s.isNullOrEmpty()
                    cardViewModel.updateCardInputStatus(adapterPosition, isNotEmpty) // 카드 인덱스와 함께 입력 상태 전달
                    Log.d("카드 입력", isNotEmpty.toString())

                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            binding.executePendingBindings()
        }
    }
}
