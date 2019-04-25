package mcis.jsu.edu.crosswordmagic;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragmentView extends Fragment implements View.OnClickListener {

    private CrosswordMagicViewModel model;

    public MainFragmentView() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(CrosswordMagicViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.main_fragment_view, container, false);

        Button b = root.findViewById(R.id.button);
        b.setOnClickListener(this);


        return root;

    }

    @Override
    public void onClick(View v) {



        ViewPager p = (ViewPager)getActivity().findViewById(R.id.pager);
        p.setCurrentItem(TabPagerAdapter.PUZZLE_FRAGMENT);

    }

}
