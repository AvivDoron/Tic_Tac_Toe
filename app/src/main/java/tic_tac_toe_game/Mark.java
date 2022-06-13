package tic_tac_toe_game;

import android.net.Uri;
import android.widget.ImageView;
import java.util.HashMap;
import java.util.Map;

/** A game mark to place on the game board */
public class Mark {
    private ImageView imageView;
    private EMark markSign;


    public Mark(ImageView imageView, EMark markSign){
        this.imageView = imageView;
        this.markSign = markSign;
    }


    public enum EMark {
        X('X'),
        O('O'),
        EMPTY(' ');

        private final Character mark;
        /** Drawables resources Configurations for the game marks */
        public static final Map<EMark, String> marksPathsMap;
        static {
            marksPathsMap = new HashMap<>();
            marksPathsMap.put(EMark.X, "android.resource://com.example.tic_tac_toe/drawable/x");
            marksPathsMap.put(EMark.O, "android.resource://com.example.tic_tac_toe/drawable/o");
        }

        EMark(final Character type) {
            this.mark = type;
        }
        public Character asChar() {
            return mark;
        }
        public boolean equals(EMark mark1, EMark mark2) {return mark1.asChar().equals(mark2);}
    }




    public boolean equals(Mark other){
        return markSign.equals(other.getMarkSign());
    }

    public boolean set(EMark markSign){
        if(isMarked())
            return false;
        imageView.setImageURI(Uri.parse(EMark.marksPathsMap.get(markSign)));
        this.markSign = markSign;
        return true;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public EMark getMarkSign() {
        return markSign;
    }

    public boolean isMarked(){
        return !markSign.equals(EMark.EMPTY);
    }

    public void clear(){
        removeImageFromImageView();
        removeMarkSign();
    }

    public void removeImageFromImageView(){
        imageView.setImageResource(0);
    }

    public void removeMarkSign(){
        markSign = EMark.EMPTY;
    }

}
