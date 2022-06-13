package local_long_term_data;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.tic_tac_toe.R;
import java.util.List;


public class GamesHistoryFragment extends Fragment {
    private Button clearAllBtn;
    private static final int VERTICAL_ITEM_SPACE = 35;

    public GamesHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_previous_games, container, false);
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView previousGamesRecycleView =  getActivity().findViewById(R.id.PreviousGames_recyclerView);
        PreviousGamesAdapter previousGamesAdapter = new PreviousGamesAdapter(getContext());
        // Attach the adapter to the recyclerview to populate items
        previousGamesRecycleView.setAdapter(previousGamesAdapter);
        // Set layout manager to position the items
        previousGamesRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
       // previousGamesRecycleView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        previousGamesRecycleView.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.bottom = VERTICAL_ITEM_SPACE;
            }
        });

    }


    public class PreviousGamesAdapter extends RecyclerView.Adapter<PreviousGamesAdapter.PreviousGameViewHolder>{
        public List<GameRecordInHistory> previousGamesList;
        private GamesRecordsViewModel gamesRecordsViewModel;


        //Constructor
        public PreviousGamesAdapter(Context context){
            gamesRecordsViewModel = new ViewModelProvider(requireActivity()).get(GamesRecordsViewModel.class);
            gamesRecordsViewModel.getGamesRecordsList().observe(getViewLifecycleOwner(), list -> {
                previousGamesList = list;
            });
            clearAllBtn = (Button) getView().findViewById(R.id.PreviousGames_clearAllBtn);
            clearAllBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gamesRecordsViewModel.clearHistory();
                    notifyDataSetChanged();
                }
            });
        }


        // ViewHolder sub-class
        public class PreviousGameViewHolder extends RecyclerView.ViewHolder {
            private TextView hostNameTV;
            private TextView hostWinningsTV;
            private TextView guestNameTV;
            private TextView guestWinningsTV;
            private TextView dateAndTimeTV;
            private View thisHolder;

            //Constructor
            public PreviousGameViewHolder(View itemView) {
                super(itemView);
                this.thisHolder = itemView;
                this.hostNameTV = (TextView) itemView.findViewById(R.id.item_previousGame_hostNameTV);
                this.hostWinningsTV = (TextView) itemView.findViewById(R.id.item_previousGame_hostWinningsTV);
                this.guestNameTV = (TextView) itemView.findViewById(R.id.item_previousGame_guestNameTV);
                this.guestWinningsTV = (TextView) itemView.findViewById(R.id.item_previousGame_guestWinningsTV);
                this.dateAndTimeTV = (TextView) itemView.findViewById(R.id.item_previousGame_dateAndTimeTV);
            }

            //Updates the view holder of the country in the given index
            public void fillData(int index){
                GameRecordInHistory gameRecordInHistory = previousGamesList.get(index);
                hostNameTV.setText(gameRecordInHistory.getHostFullName());
                hostWinningsTV.setText(""+gameRecordInHistory.getHostWinningsNum());
                guestNameTV.setText(gameRecordInHistory.getGuestFullName());
                guestWinningsTV.setText(""+gameRecordInHistory.getGuestWinningsNum());
                dateAndTimeTV.setText(gamesRecordsViewModel.adjustDateAndTimeForDisplay(gameRecordInHistory.getDateAndTimeInMilliSecs()));

                thisHolder.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        gamesRecordsViewModel.removeGameRecord(index);
                        notifyItemRemoved(index);
                        notifyDataSetChanged();
                        return false;
                    }
                });
            }
        }



        @NonNull
        @Override
        public PreviousGameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View countryView = inflater.inflate(R.layout.item_previous_game, parent, false);

            // Return a new holder instance
            PreviousGamesAdapter.PreviousGameViewHolder viewHolder = new PreviousGamesAdapter.PreviousGameViewHolder(countryView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull PreviousGamesAdapter.PreviousGameViewHolder holder, int position) {
            holder.fillData(position);
        }

        @Override
        public int getItemCount() {
            return previousGamesList.size();
        }

    }

}