package tic_tac_toe_game;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.tic_tac_toe.R;
import com.example.tic_tac_toe.User;


public class GameFragment extends Fragment implements IGameManager.IGameManagerListener {
    private final int boardDimension = 3;
    private Board board;
    private GameFragmentListener listener;
    IGameManager igameManager;
    private TextView playerBarTV;
    private TextView RivalBarTV;
    private View playerBarPlaceHolder;
    private View rivalBarPlaceHolder;
    private Drawable highlightedPlaceHolder;
    private Drawable nonHighlightedPlaceHolder;
    private Game game;





    public GameFragment() {
        // Required empty public constructor
    }


    public interface GameFragmentListener{
        User getGameHost();
        User getGameGuest();
        Game.EGameType getGameType();
        void handleRivalLeftEvent();
        void informCurrentPlayerTurnArrived();
        void informCurrentPlayerTurnFinished();
        void openGiveUpConfirmationDialog();
    }




    @Override
    public void onAttach(@NonNull Context context) {
        try{
            this.listener = (GameFragmentListener)context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " +
                    context.getClass().getName() +
                    " must implements the interface 'GameFragmentListener'");
        }
        super.onAttach(context);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.board = new Board(boardDimension);
        this.highlightedPlaceHolder = ContextCompat.getDrawable(getActivity(), R.drawable.highlighted_faded_placeholder);
        this.nonHighlightedPlaceHolder = ContextCompat.getDrawable(getActivity(), R.drawable.faded_placeholder);

    }


    public void applyPlayerGiveUpProcedure(){
        igameManager.applyPlayerGiveUpProcedure();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TableLayout boardTableLayout = (TableLayout) view.findViewById(R.id.Game_Board_TableLayout);
        for(int i = 0 ; i < boardTableLayout.getChildCount() ; i++){
            TableRow tableRow = (TableRow) boardTableLayout.getChildAt(i);
            for(int j = 0 ; j < tableRow.getChildCount() ; j++){
                ImageView tableCell = (ImageView) tableRow.getChildAt(j);
                setGameCellImageViewOnClickListener(i, j, tableCell);
                board.initMark(i, j, new Mark(tableCell, Mark.EMark.EMPTY));
            }
        }

        Button giveUpBtn = (Button) view.findViewById(R.id.Game_GiveUpBtn);
        giveUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.openGiveUpConfirmationDialog();
            }
        });

        User gameHost = listener.getGameHost();
        User gameGuest = listener.getGameGuest();
        Game.EGameType gameType= listener.getGameType();
        this.game = new Game(gameHost, gameGuest, board, gameType);
        igameManager = GameFactory.getGameManagerInstance(game, this);
        initPlayersBar(view);
    }



    public void giveRivalExtraPoint(){
        igameManager.giveRivalExtraPoint();
    }




    /**
     *  Handle the cases: player is absent / network disconnected
     */
    public void handleTurnTimeOutEvent(){
        igameManager.handleTurnTimeOutEvent();
    }



    private void initPlayersBar(@NonNull View view){
        playerBarTV = (TextView) view.findViewById(R.id.Game_Player1TV);
        RivalBarTV = (TextView) view.findViewById(R.id.Game_Player2TV);
        playerBarPlaceHolder= view.findViewById(R.id.Game_Player1CL);
        rivalBarPlaceHolder = view.findViewById(R.id.Game_Player2CL);
        Player player = igameManager.getPlayer();
        Player rivalPlayer = igameManager.getRival();
        playerBarTV.setText(player.getFullName() + ": " + player.getWinningsNum());
        RivalBarTV.setText(rivalPlayer.getFullName() + ": " + rivalPlayer.getWinningsNum());
        if(igameManager.isItCurrentPlayerTurn()) {
            setBarHighlighting(playerBarPlaceHolder, true);
        }
        else {
            setBarHighlighting(rivalBarPlaceHolder, true);
        }
    }


    /**
     * Init a cell in the table layout - adds onClickListener
     * @param i
     * @param j
     * @param imageView
     */
    private void setGameCellImageViewOnClickListener(int i, int j, ImageView imageView){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(board.isEmpty(i,j) && igameManager.isItCurrentPlayerTurn()) {
                    igameManager.makeMove(i, j);

                }
            }
        });
    }




    //-------------------------------------- IGameManagerListener methods implementation -------------------------------------------------------//



    @Override
    public void updatePlayerWinningsAmount(Player player) {
        Player rivalPlayer = igameManager.getRival();
        TextView playerBar = player.equals(rivalPlayer) ? RivalBarTV : playerBarTV;
        playerBar.setText(player.getFullName() + ": " + player.getWinningsNum());
    }

    @Override
    public void handleCurrentPlayerTurnArrived() {
        setBarHighlighting(playerBarPlaceHolder, true);
        setBarHighlighting(rivalBarPlaceHolder, false);
        listener.informCurrentPlayerTurnArrived();
    }

    @Override
    public void handleCurrentPlayerTurnFinished() {
        setBarHighlighting(playerBarPlaceHolder, false);
        setBarHighlighting(rivalBarPlaceHolder, true);
        listener.informCurrentPlayerTurnFinished();
    }

    @Override
    public void handleRivalLeftEvent() {
        listener.handleRivalLeftEvent();
    }


    private void setBarHighlighting(View barPlaceHolder, boolean highlight){
        Drawable drawable = highlight ? highlightedPlaceHolder : nonHighlightedPlaceHolder;
        barPlaceHolder.setBackground(drawable);
    }

}
