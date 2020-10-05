package de.jClipCorn;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;

public class TestForm extends JFrame {
	public static void main(String[] arg) {
		(new TestForm()).setVisible(true);
	}

	public TestForm() {
		initComponents();
	}

	private void enableAll() {
		setPanelEnabled(this, true);
	}

	private void disableAll() {
		setPanelEnabled(this, false);
	}

	public void setPanelEnabled(Container panel, Boolean isEnabled)
	{
		// panel.setEnabled(isEnabled);

		Component[] components = panel.getComponents();

		for (Component component : components)
		{
			if (component instanceof Container) setPanelEnabled((Container) component, isEnabled);

			if (component == btnOn)  continue;
			if (component == btnOff) continue;
			component.setEnabled(isEnabled);
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		btnOn = new JButton();
		btnOff = new JButton();
		button2 = new JButton();
		button3 = new JButton();
		button4 = new JButton();
		button5 = new JButton();
		button6 = new JButton();
		button7 = new JButton();
		button8 = new JButton();
		button9 = new JButton();
		button10 = new JButton();
		button11 = new JButton();
		button12 = new JButton();
		button13 = new JButton();
		button14 = new JButton();
		button15 = new JButton();
		button16 = new JButton();
		button17 = new JButton();
		button18 = new JButton();
		button19 = new JButton();
		button20 = new JButton();
		button21 = new JButton();
		button22 = new JButton();
		button23 = new JButton();
		button24 = new JButton();
		button25 = new JButton();
		button26 = new JButton();
		button27 = new JButton();
		button28 = new JButton();
		button29 = new JButton();
		button30 = new JButton();
		button31 = new JButton();
		button32 = new JButton();
		button213 = new JButton();
		button33 = new JButton();
		button34 = new JButton();
		button35 = new JButton();
		button36 = new JButton();
		button37 = new JButton();
		button38 = new JButton();
		button39 = new JButton();
		button40 = new JButton();
		button41 = new JButton();
		button42 = new JButton();
		button43 = new JButton();
		button44 = new JButton();
		button45 = new JButton();
		button46 = new JButton();
		button47 = new JButton();
		button214 = new JButton();
		button48 = new JButton();
		button49 = new JButton();
		button50 = new JButton();
		button51 = new JButton();
		button52 = new JButton();
		button53 = new JButton();
		button54 = new JButton();
		button55 = new JButton();
		button56 = new JButton();
		button57 = new JButton();
		button58 = new JButton();
		button59 = new JButton();
		button60 = new JButton();
		button61 = new JButton();
		button62 = new JButton();
		button215 = new JButton();
		button63 = new JButton();
		button64 = new JButton();
		button65 = new JButton();
		button66 = new JButton();
		button67 = new JButton();
		button68 = new JButton();
		button69 = new JButton();
		button70 = new JButton();
		button71 = new JButton();
		button72 = new JButton();
		button73 = new JButton();
		button74 = new JButton();
		button75 = new JButton();
		button76 = new JButton();
		button77 = new JButton();
		button216 = new JButton();
		button78 = new JButton();
		button79 = new JButton();
		button80 = new JButton();
		button81 = new JButton();
		button82 = new JButton();
		button83 = new JButton();
		button84 = new JButton();
		button85 = new JButton();
		button86 = new JButton();
		button87 = new JButton();
		button88 = new JButton();
		button89 = new JButton();
		button90 = new JButton();
		button91 = new JButton();
		button92 = new JButton();
		button217 = new JButton();
		button93 = new JButton();
		button94 = new JButton();
		button95 = new JButton();
		button96 = new JButton();
		button97 = new JButton();
		button98 = new JButton();
		button99 = new JButton();
		button100 = new JButton();
		button101 = new JButton();
		button102 = new JButton();
		button103 = new JButton();
		button104 = new JButton();
		button105 = new JButton();
		button106 = new JButton();
		button107 = new JButton();
		button218 = new JButton();
		button108 = new JButton();
		button109 = new JButton();
		button110 = new JButton();
		button111 = new JButton();
		button112 = new JButton();
		button113 = new JButton();
		button114 = new JButton();
		button115 = new JButton();
		button116 = new JButton();
		button117 = new JButton();
		button118 = new JButton();
		button119 = new JButton();
		button120 = new JButton();
		button121 = new JButton();
		button122 = new JButton();
		button219 = new JButton();
		button123 = new JButton();
		button124 = new JButton();
		button125 = new JButton();
		button126 = new JButton();
		button127 = new JButton();
		button128 = new JButton();
		button129 = new JButton();
		button130 = new JButton();
		button131 = new JButton();
		button132 = new JButton();
		button133 = new JButton();
		button134 = new JButton();
		button135 = new JButton();
		button136 = new JButton();
		button137 = new JButton();
		button220 = new JButton();
		button138 = new JButton();
		button139 = new JButton();
		button140 = new JButton();
		button141 = new JButton();
		button142 = new JButton();
		button143 = new JButton();
		button144 = new JButton();
		button145 = new JButton();
		button146 = new JButton();
		button147 = new JButton();
		button148 = new JButton();
		button149 = new JButton();
		button150 = new JButton();
		button151 = new JButton();
		button152 = new JButton();
		button221 = new JButton();
		button153 = new JButton();
		button154 = new JButton();
		button155 = new JButton();
		button156 = new JButton();
		button157 = new JButton();
		button158 = new JButton();
		button159 = new JButton();
		button160 = new JButton();
		button161 = new JButton();
		button162 = new JButton();
		button163 = new JButton();
		button164 = new JButton();
		button165 = new JButton();
		button166 = new JButton();
		button167 = new JButton();
		button222 = new JButton();
		button168 = new JButton();
		button169 = new JButton();
		button170 = new JButton();
		button171 = new JButton();
		button172 = new JButton();
		button173 = new JButton();
		button174 = new JButton();
		button175 = new JButton();
		button176 = new JButton();
		button177 = new JButton();
		button178 = new JButton();
		button179 = new JButton();
		button180 = new JButton();
		button181 = new JButton();
		button182 = new JButton();
		button223 = new JButton();
		button183 = new JButton();
		button184 = new JButton();
		button185 = new JButton();
		button186 = new JButton();
		button187 = new JButton();
		button188 = new JButton();
		button189 = new JButton();
		button190 = new JButton();
		button191 = new JButton();
		button192 = new JButton();
		button193 = new JButton();
		button194 = new JButton();
		button195 = new JButton();
		button196 = new JButton();
		button197 = new JButton();
		button224 = new JButton();
		button198 = new JButton();
		button199 = new JButton();
		button200 = new JButton();
		button201 = new JButton();
		button202 = new JButton();
		button203 = new JButton();
		button204 = new JButton();
		button205 = new JButton();
		button206 = new JButton();
		button207 = new JButton();
		button208 = new JButton();
		button209 = new JButton();
		button210 = new JButton();
		button211 = new JButton();
		button212 = new JButton();
		button225 = new JButton();

		//======== this ========
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, 15*(default, $lcgap), default, $ugap", //$NON-NLS-1$
			"$ugap, 15*(default, $lgap), default, $ugap")); //$NON-NLS-1$

		//---- btnOn ----
		btnOn.setText("ON"); //$NON-NLS-1$
		btnOn.addActionListener(e -> enableAll());
		contentPane.add(btnOn, CC.xywh(2, 2, 5, 1));

		//---- btnOff ----
		btnOff.setText("OFF"); //$NON-NLS-1$
		btnOff.addActionListener(e -> disableAll());
		contentPane.add(btnOff, CC.xywh(8, 2, 5, 1));

		//---- button2 ----
		button2.setText("text"); //$NON-NLS-1$
		contentPane.add(button2, CC.xy(2, 6));

		//---- button3 ----
		button3.setText("text"); //$NON-NLS-1$
		contentPane.add(button3, CC.xy(4, 6));

		//---- button4 ----
		button4.setText("text"); //$NON-NLS-1$
		contentPane.add(button4, CC.xy(6, 6));

		//---- button5 ----
		button5.setText("text"); //$NON-NLS-1$
		contentPane.add(button5, CC.xy(8, 6));

		//---- button6 ----
		button6.setText("text"); //$NON-NLS-1$
		contentPane.add(button6, CC.xy(10, 6));

		//---- button7 ----
		button7.setText("text"); //$NON-NLS-1$
		contentPane.add(button7, CC.xy(12, 6));

		//---- button8 ----
		button8.setText("text"); //$NON-NLS-1$
		contentPane.add(button8, CC.xy(14, 6));

		//---- button9 ----
		button9.setText("text"); //$NON-NLS-1$
		contentPane.add(button9, CC.xy(16, 6));

		//---- button10 ----
		button10.setText("text"); //$NON-NLS-1$
		contentPane.add(button10, CC.xy(18, 6));

		//---- button11 ----
		button11.setText("text"); //$NON-NLS-1$
		contentPane.add(button11, CC.xy(20, 6));

		//---- button12 ----
		button12.setText("text"); //$NON-NLS-1$
		contentPane.add(button12, CC.xy(22, 6));

		//---- button13 ----
		button13.setText("text"); //$NON-NLS-1$
		contentPane.add(button13, CC.xy(24, 6));

		//---- button14 ----
		button14.setText("text"); //$NON-NLS-1$
		contentPane.add(button14, CC.xy(26, 6));

		//---- button15 ----
		button15.setText("text"); //$NON-NLS-1$
		contentPane.add(button15, CC.xy(28, 6));

		//---- button16 ----
		button16.setText("text"); //$NON-NLS-1$
		contentPane.add(button16, CC.xy(30, 6));

		//---- button17 ----
		button17.setText("text"); //$NON-NLS-1$
		contentPane.add(button17, CC.xy(32, 6));

		//---- button18 ----
		button18.setText("text"); //$NON-NLS-1$
		contentPane.add(button18, CC.xy(2, 8));

		//---- button19 ----
		button19.setText("text"); //$NON-NLS-1$
		contentPane.add(button19, CC.xy(4, 8));

		//---- button20 ----
		button20.setText("text"); //$NON-NLS-1$
		contentPane.add(button20, CC.xy(6, 8));

		//---- button21 ----
		button21.setText("text"); //$NON-NLS-1$
		contentPane.add(button21, CC.xy(8, 8));

		//---- button22 ----
		button22.setText("text"); //$NON-NLS-1$
		contentPane.add(button22, CC.xy(10, 8));

		//---- button23 ----
		button23.setText("text"); //$NON-NLS-1$
		contentPane.add(button23, CC.xy(12, 8));

		//---- button24 ----
		button24.setText("text"); //$NON-NLS-1$
		contentPane.add(button24, CC.xy(14, 8));

		//---- button25 ----
		button25.setText("text"); //$NON-NLS-1$
		contentPane.add(button25, CC.xy(16, 8));

		//---- button26 ----
		button26.setText("text"); //$NON-NLS-1$
		contentPane.add(button26, CC.xy(18, 8));

		//---- button27 ----
		button27.setText("text"); //$NON-NLS-1$
		contentPane.add(button27, CC.xy(20, 8));

		//---- button28 ----
		button28.setText("text"); //$NON-NLS-1$
		contentPane.add(button28, CC.xy(22, 8));

		//---- button29 ----
		button29.setText("text"); //$NON-NLS-1$
		contentPane.add(button29, CC.xy(24, 8));

		//---- button30 ----
		button30.setText("text"); //$NON-NLS-1$
		contentPane.add(button30, CC.xy(26, 8));

		//---- button31 ----
		button31.setText("text"); //$NON-NLS-1$
		contentPane.add(button31, CC.xy(28, 8));

		//---- button32 ----
		button32.setText("text"); //$NON-NLS-1$
		contentPane.add(button32, CC.xy(30, 8));

		//---- button213 ----
		button213.setText("text"); //$NON-NLS-1$
		contentPane.add(button213, CC.xy(32, 8));

		//---- button33 ----
		button33.setText("text"); //$NON-NLS-1$
		contentPane.add(button33, CC.xy(2, 10));

		//---- button34 ----
		button34.setText("text"); //$NON-NLS-1$
		contentPane.add(button34, CC.xy(4, 10));

		//---- button35 ----
		button35.setText("text"); //$NON-NLS-1$
		contentPane.add(button35, CC.xy(6, 10));

		//---- button36 ----
		button36.setText("text"); //$NON-NLS-1$
		contentPane.add(button36, CC.xy(8, 10));

		//---- button37 ----
		button37.setText("text"); //$NON-NLS-1$
		contentPane.add(button37, CC.xy(10, 10));

		//---- button38 ----
		button38.setText("text"); //$NON-NLS-1$
		contentPane.add(button38, CC.xy(12, 10));

		//---- button39 ----
		button39.setText("text"); //$NON-NLS-1$
		contentPane.add(button39, CC.xy(14, 10));

		//---- button40 ----
		button40.setText("text"); //$NON-NLS-1$
		contentPane.add(button40, CC.xy(16, 10));

		//---- button41 ----
		button41.setText("text"); //$NON-NLS-1$
		contentPane.add(button41, CC.xy(18, 10));

		//---- button42 ----
		button42.setText("text"); //$NON-NLS-1$
		contentPane.add(button42, CC.xy(20, 10));

		//---- button43 ----
		button43.setText("text"); //$NON-NLS-1$
		contentPane.add(button43, CC.xy(22, 10));

		//---- button44 ----
		button44.setText("text"); //$NON-NLS-1$
		contentPane.add(button44, CC.xy(24, 10));

		//---- button45 ----
		button45.setText("text"); //$NON-NLS-1$
		contentPane.add(button45, CC.xy(26, 10));

		//---- button46 ----
		button46.setText("text"); //$NON-NLS-1$
		contentPane.add(button46, CC.xy(28, 10));

		//---- button47 ----
		button47.setText("text"); //$NON-NLS-1$
		contentPane.add(button47, CC.xy(30, 10));

		//---- button214 ----
		button214.setText("text"); //$NON-NLS-1$
		contentPane.add(button214, CC.xy(32, 10));

		//---- button48 ----
		button48.setText("text"); //$NON-NLS-1$
		contentPane.add(button48, CC.xy(2, 12));

		//---- button49 ----
		button49.setText("text"); //$NON-NLS-1$
		contentPane.add(button49, CC.xy(4, 12));

		//---- button50 ----
		button50.setText("text"); //$NON-NLS-1$
		contentPane.add(button50, CC.xy(6, 12));

		//---- button51 ----
		button51.setText("text"); //$NON-NLS-1$
		contentPane.add(button51, CC.xy(8, 12));

		//---- button52 ----
		button52.setText("text"); //$NON-NLS-1$
		contentPane.add(button52, CC.xy(10, 12));

		//---- button53 ----
		button53.setText("text"); //$NON-NLS-1$
		contentPane.add(button53, CC.xy(12, 12));

		//---- button54 ----
		button54.setText("text"); //$NON-NLS-1$
		contentPane.add(button54, CC.xy(14, 12));

		//---- button55 ----
		button55.setText("text"); //$NON-NLS-1$
		contentPane.add(button55, CC.xy(16, 12));

		//---- button56 ----
		button56.setText("text"); //$NON-NLS-1$
		contentPane.add(button56, CC.xy(18, 12));

		//---- button57 ----
		button57.setText("text"); //$NON-NLS-1$
		contentPane.add(button57, CC.xy(20, 12));

		//---- button58 ----
		button58.setText("text"); //$NON-NLS-1$
		contentPane.add(button58, CC.xy(22, 12));

		//---- button59 ----
		button59.setText("text"); //$NON-NLS-1$
		contentPane.add(button59, CC.xy(24, 12));

		//---- button60 ----
		button60.setText("text"); //$NON-NLS-1$
		contentPane.add(button60, CC.xy(26, 12));

		//---- button61 ----
		button61.setText("text"); //$NON-NLS-1$
		contentPane.add(button61, CC.xy(28, 12));

		//---- button62 ----
		button62.setText("text"); //$NON-NLS-1$
		contentPane.add(button62, CC.xy(30, 12));

		//---- button215 ----
		button215.setText("text"); //$NON-NLS-1$
		contentPane.add(button215, CC.xy(32, 12));

		//---- button63 ----
		button63.setText("text"); //$NON-NLS-1$
		contentPane.add(button63, CC.xy(2, 14));

		//---- button64 ----
		button64.setText("text"); //$NON-NLS-1$
		contentPane.add(button64, CC.xy(4, 14));

		//---- button65 ----
		button65.setText("text"); //$NON-NLS-1$
		contentPane.add(button65, CC.xy(6, 14));

		//---- button66 ----
		button66.setText("text"); //$NON-NLS-1$
		contentPane.add(button66, CC.xy(8, 14));

		//---- button67 ----
		button67.setText("text"); //$NON-NLS-1$
		contentPane.add(button67, CC.xy(10, 14));

		//---- button68 ----
		button68.setText("text"); //$NON-NLS-1$
		contentPane.add(button68, CC.xy(12, 14));

		//---- button69 ----
		button69.setText("text"); //$NON-NLS-1$
		contentPane.add(button69, CC.xy(14, 14));

		//---- button70 ----
		button70.setText("text"); //$NON-NLS-1$
		contentPane.add(button70, CC.xy(16, 14));

		//---- button71 ----
		button71.setText("text"); //$NON-NLS-1$
		contentPane.add(button71, CC.xy(18, 14));

		//---- button72 ----
		button72.setText("text"); //$NON-NLS-1$
		contentPane.add(button72, CC.xy(20, 14));

		//---- button73 ----
		button73.setText("text"); //$NON-NLS-1$
		contentPane.add(button73, CC.xy(22, 14));

		//---- button74 ----
		button74.setText("text"); //$NON-NLS-1$
		contentPane.add(button74, CC.xy(24, 14));

		//---- button75 ----
		button75.setText("text"); //$NON-NLS-1$
		contentPane.add(button75, CC.xy(26, 14));

		//---- button76 ----
		button76.setText("text"); //$NON-NLS-1$
		contentPane.add(button76, CC.xy(28, 14));

		//---- button77 ----
		button77.setText("text"); //$NON-NLS-1$
		contentPane.add(button77, CC.xy(30, 14));

		//---- button216 ----
		button216.setText("text"); //$NON-NLS-1$
		contentPane.add(button216, CC.xy(32, 14));

		//---- button78 ----
		button78.setText("text"); //$NON-NLS-1$
		contentPane.add(button78, CC.xy(2, 16));

		//---- button79 ----
		button79.setText("text"); //$NON-NLS-1$
		contentPane.add(button79, CC.xy(4, 16));

		//---- button80 ----
		button80.setText("text"); //$NON-NLS-1$
		contentPane.add(button80, CC.xy(6, 16));

		//---- button81 ----
		button81.setText("text"); //$NON-NLS-1$
		contentPane.add(button81, CC.xy(8, 16));

		//---- button82 ----
		button82.setText("text"); //$NON-NLS-1$
		contentPane.add(button82, CC.xy(10, 16));

		//---- button83 ----
		button83.setText("text"); //$NON-NLS-1$
		contentPane.add(button83, CC.xy(12, 16));

		//---- button84 ----
		button84.setText("text"); //$NON-NLS-1$
		contentPane.add(button84, CC.xy(14, 16));

		//---- button85 ----
		button85.setText("text"); //$NON-NLS-1$
		contentPane.add(button85, CC.xy(16, 16));

		//---- button86 ----
		button86.setText("text"); //$NON-NLS-1$
		contentPane.add(button86, CC.xy(18, 16));

		//---- button87 ----
		button87.setText("text"); //$NON-NLS-1$
		contentPane.add(button87, CC.xy(20, 16));

		//---- button88 ----
		button88.setText("text"); //$NON-NLS-1$
		contentPane.add(button88, CC.xy(22, 16));

		//---- button89 ----
		button89.setText("text"); //$NON-NLS-1$
		contentPane.add(button89, CC.xy(24, 16));

		//---- button90 ----
		button90.setText("text"); //$NON-NLS-1$
		contentPane.add(button90, CC.xy(26, 16));

		//---- button91 ----
		button91.setText("text"); //$NON-NLS-1$
		contentPane.add(button91, CC.xy(28, 16));

		//---- button92 ----
		button92.setText("text"); //$NON-NLS-1$
		contentPane.add(button92, CC.xy(30, 16));

		//---- button217 ----
		button217.setText("text"); //$NON-NLS-1$
		contentPane.add(button217, CC.xy(32, 16));

		//---- button93 ----
		button93.setText("text"); //$NON-NLS-1$
		contentPane.add(button93, CC.xy(2, 18));

		//---- button94 ----
		button94.setText("text"); //$NON-NLS-1$
		contentPane.add(button94, CC.xy(4, 18));

		//---- button95 ----
		button95.setText("text"); //$NON-NLS-1$
		contentPane.add(button95, CC.xy(6, 18));

		//---- button96 ----
		button96.setText("text"); //$NON-NLS-1$
		contentPane.add(button96, CC.xy(8, 18));

		//---- button97 ----
		button97.setText("text"); //$NON-NLS-1$
		contentPane.add(button97, CC.xy(10, 18));

		//---- button98 ----
		button98.setText("text"); //$NON-NLS-1$
		contentPane.add(button98, CC.xy(12, 18));

		//---- button99 ----
		button99.setText("text"); //$NON-NLS-1$
		contentPane.add(button99, CC.xy(14, 18));

		//---- button100 ----
		button100.setText("text"); //$NON-NLS-1$
		contentPane.add(button100, CC.xy(16, 18));

		//---- button101 ----
		button101.setText("text"); //$NON-NLS-1$
		contentPane.add(button101, CC.xy(18, 18));

		//---- button102 ----
		button102.setText("text"); //$NON-NLS-1$
		contentPane.add(button102, CC.xy(20, 18));

		//---- button103 ----
		button103.setText("text"); //$NON-NLS-1$
		contentPane.add(button103, CC.xy(22, 18));

		//---- button104 ----
		button104.setText("text"); //$NON-NLS-1$
		contentPane.add(button104, CC.xy(24, 18));

		//---- button105 ----
		button105.setText("text"); //$NON-NLS-1$
		contentPane.add(button105, CC.xy(26, 18));

		//---- button106 ----
		button106.setText("text"); //$NON-NLS-1$
		contentPane.add(button106, CC.xy(28, 18));

		//---- button107 ----
		button107.setText("text"); //$NON-NLS-1$
		contentPane.add(button107, CC.xy(30, 18));

		//---- button218 ----
		button218.setText("text"); //$NON-NLS-1$
		contentPane.add(button218, CC.xy(32, 18));

		//---- button108 ----
		button108.setText("text"); //$NON-NLS-1$
		contentPane.add(button108, CC.xy(2, 20));

		//---- button109 ----
		button109.setText("text"); //$NON-NLS-1$
		contentPane.add(button109, CC.xy(4, 20));

		//---- button110 ----
		button110.setText("text"); //$NON-NLS-1$
		contentPane.add(button110, CC.xy(6, 20));

		//---- button111 ----
		button111.setText("text"); //$NON-NLS-1$
		contentPane.add(button111, CC.xy(8, 20));

		//---- button112 ----
		button112.setText("text"); //$NON-NLS-1$
		contentPane.add(button112, CC.xy(10, 20));

		//---- button113 ----
		button113.setText("text"); //$NON-NLS-1$
		contentPane.add(button113, CC.xy(12, 20));

		//---- button114 ----
		button114.setText("text"); //$NON-NLS-1$
		contentPane.add(button114, CC.xy(14, 20));

		//---- button115 ----
		button115.setText("text"); //$NON-NLS-1$
		contentPane.add(button115, CC.xy(16, 20));

		//---- button116 ----
		button116.setText("text"); //$NON-NLS-1$
		contentPane.add(button116, CC.xy(18, 20));

		//---- button117 ----
		button117.setText("text"); //$NON-NLS-1$
		contentPane.add(button117, CC.xy(20, 20));

		//---- button118 ----
		button118.setText("text"); //$NON-NLS-1$
		contentPane.add(button118, CC.xy(22, 20));

		//---- button119 ----
		button119.setText("text"); //$NON-NLS-1$
		contentPane.add(button119, CC.xy(24, 20));

		//---- button120 ----
		button120.setText("text"); //$NON-NLS-1$
		contentPane.add(button120, CC.xy(26, 20));

		//---- button121 ----
		button121.setText("text"); //$NON-NLS-1$
		contentPane.add(button121, CC.xy(28, 20));

		//---- button122 ----
		button122.setText("text"); //$NON-NLS-1$
		contentPane.add(button122, CC.xy(30, 20));

		//---- button219 ----
		button219.setText("text"); //$NON-NLS-1$
		contentPane.add(button219, CC.xy(32, 20));

		//---- button123 ----
		button123.setText("text"); //$NON-NLS-1$
		contentPane.add(button123, CC.xy(2, 22));

		//---- button124 ----
		button124.setText("text"); //$NON-NLS-1$
		contentPane.add(button124, CC.xy(4, 22));

		//---- button125 ----
		button125.setText("text"); //$NON-NLS-1$
		contentPane.add(button125, CC.xy(6, 22));

		//---- button126 ----
		button126.setText("text"); //$NON-NLS-1$
		contentPane.add(button126, CC.xy(8, 22));

		//---- button127 ----
		button127.setText("text"); //$NON-NLS-1$
		contentPane.add(button127, CC.xy(10, 22));

		//---- button128 ----
		button128.setText("text"); //$NON-NLS-1$
		contentPane.add(button128, CC.xy(12, 22));

		//---- button129 ----
		button129.setText("text"); //$NON-NLS-1$
		contentPane.add(button129, CC.xy(14, 22));

		//---- button130 ----
		button130.setText("text"); //$NON-NLS-1$
		contentPane.add(button130, CC.xy(16, 22));

		//---- button131 ----
		button131.setText("text"); //$NON-NLS-1$
		contentPane.add(button131, CC.xy(18, 22));

		//---- button132 ----
		button132.setText("text"); //$NON-NLS-1$
		contentPane.add(button132, CC.xy(20, 22));

		//---- button133 ----
		button133.setText("text"); //$NON-NLS-1$
		contentPane.add(button133, CC.xy(22, 22));

		//---- button134 ----
		button134.setText("text"); //$NON-NLS-1$
		contentPane.add(button134, CC.xy(24, 22));

		//---- button135 ----
		button135.setText("text"); //$NON-NLS-1$
		contentPane.add(button135, CC.xy(26, 22));

		//---- button136 ----
		button136.setText("text"); //$NON-NLS-1$
		contentPane.add(button136, CC.xy(28, 22));

		//---- button137 ----
		button137.setText("text"); //$NON-NLS-1$
		contentPane.add(button137, CC.xy(30, 22));

		//---- button220 ----
		button220.setText("text"); //$NON-NLS-1$
		contentPane.add(button220, CC.xy(32, 22));

		//---- button138 ----
		button138.setText("text"); //$NON-NLS-1$
		contentPane.add(button138, CC.xy(2, 24));

		//---- button139 ----
		button139.setText("text"); //$NON-NLS-1$
		contentPane.add(button139, CC.xy(4, 24));

		//---- button140 ----
		button140.setText("text"); //$NON-NLS-1$
		contentPane.add(button140, CC.xy(6, 24));

		//---- button141 ----
		button141.setText("text"); //$NON-NLS-1$
		contentPane.add(button141, CC.xy(8, 24));

		//---- button142 ----
		button142.setText("text"); //$NON-NLS-1$
		contentPane.add(button142, CC.xy(10, 24));

		//---- button143 ----
		button143.setText("text"); //$NON-NLS-1$
		contentPane.add(button143, CC.xy(12, 24));

		//---- button144 ----
		button144.setText("text"); //$NON-NLS-1$
		contentPane.add(button144, CC.xy(14, 24));

		//---- button145 ----
		button145.setText("text"); //$NON-NLS-1$
		contentPane.add(button145, CC.xy(16, 24));

		//---- button146 ----
		button146.setText("text"); //$NON-NLS-1$
		contentPane.add(button146, CC.xy(18, 24));

		//---- button147 ----
		button147.setText("text"); //$NON-NLS-1$
		contentPane.add(button147, CC.xy(20, 24));

		//---- button148 ----
		button148.setText("text"); //$NON-NLS-1$
		contentPane.add(button148, CC.xy(22, 24));

		//---- button149 ----
		button149.setText("text"); //$NON-NLS-1$
		contentPane.add(button149, CC.xy(24, 24));

		//---- button150 ----
		button150.setText("text"); //$NON-NLS-1$
		contentPane.add(button150, CC.xy(26, 24));

		//---- button151 ----
		button151.setText("text"); //$NON-NLS-1$
		contentPane.add(button151, CC.xy(28, 24));

		//---- button152 ----
		button152.setText("text"); //$NON-NLS-1$
		contentPane.add(button152, CC.xy(30, 24));

		//---- button221 ----
		button221.setText("text"); //$NON-NLS-1$
		contentPane.add(button221, CC.xy(32, 24));

		//---- button153 ----
		button153.setText("text"); //$NON-NLS-1$
		contentPane.add(button153, CC.xy(2, 26));

		//---- button154 ----
		button154.setText("text"); //$NON-NLS-1$
		contentPane.add(button154, CC.xy(4, 26));

		//---- button155 ----
		button155.setText("text"); //$NON-NLS-1$
		contentPane.add(button155, CC.xy(6, 26));

		//---- button156 ----
		button156.setText("text"); //$NON-NLS-1$
		contentPane.add(button156, CC.xy(8, 26));

		//---- button157 ----
		button157.setText("text"); //$NON-NLS-1$
		contentPane.add(button157, CC.xy(10, 26));

		//---- button158 ----
		button158.setText("text"); //$NON-NLS-1$
		contentPane.add(button158, CC.xy(12, 26));

		//---- button159 ----
		button159.setText("text"); //$NON-NLS-1$
		contentPane.add(button159, CC.xy(14, 26));

		//---- button160 ----
		button160.setText("text"); //$NON-NLS-1$
		contentPane.add(button160, CC.xy(16, 26));

		//---- button161 ----
		button161.setText("text"); //$NON-NLS-1$
		contentPane.add(button161, CC.xy(18, 26));

		//---- button162 ----
		button162.setText("text"); //$NON-NLS-1$
		contentPane.add(button162, CC.xy(20, 26));

		//---- button163 ----
		button163.setText("text"); //$NON-NLS-1$
		contentPane.add(button163, CC.xy(22, 26));

		//---- button164 ----
		button164.setText("text"); //$NON-NLS-1$
		contentPane.add(button164, CC.xy(24, 26));

		//---- button165 ----
		button165.setText("text"); //$NON-NLS-1$
		contentPane.add(button165, CC.xy(26, 26));

		//---- button166 ----
		button166.setText("text"); //$NON-NLS-1$
		contentPane.add(button166, CC.xy(28, 26));

		//---- button167 ----
		button167.setText("text"); //$NON-NLS-1$
		contentPane.add(button167, CC.xy(30, 26));

		//---- button222 ----
		button222.setText("text"); //$NON-NLS-1$
		contentPane.add(button222, CC.xy(32, 26));

		//---- button168 ----
		button168.setText("text"); //$NON-NLS-1$
		contentPane.add(button168, CC.xy(2, 28));

		//---- button169 ----
		button169.setText("text"); //$NON-NLS-1$
		contentPane.add(button169, CC.xy(4, 28));

		//---- button170 ----
		button170.setText("text"); //$NON-NLS-1$
		contentPane.add(button170, CC.xy(6, 28));

		//---- button171 ----
		button171.setText("text"); //$NON-NLS-1$
		contentPane.add(button171, CC.xy(8, 28));

		//---- button172 ----
		button172.setText("text"); //$NON-NLS-1$
		contentPane.add(button172, CC.xy(10, 28));

		//---- button173 ----
		button173.setText("text"); //$NON-NLS-1$
		contentPane.add(button173, CC.xy(12, 28));

		//---- button174 ----
		button174.setText("text"); //$NON-NLS-1$
		contentPane.add(button174, CC.xy(14, 28));

		//---- button175 ----
		button175.setText("text"); //$NON-NLS-1$
		contentPane.add(button175, CC.xy(16, 28));

		//---- button176 ----
		button176.setText("text"); //$NON-NLS-1$
		contentPane.add(button176, CC.xy(18, 28));

		//---- button177 ----
		button177.setText("text"); //$NON-NLS-1$
		contentPane.add(button177, CC.xy(20, 28));

		//---- button178 ----
		button178.setText("text"); //$NON-NLS-1$
		contentPane.add(button178, CC.xy(22, 28));

		//---- button179 ----
		button179.setText("text"); //$NON-NLS-1$
		contentPane.add(button179, CC.xy(24, 28));

		//---- button180 ----
		button180.setText("text"); //$NON-NLS-1$
		contentPane.add(button180, CC.xy(26, 28));

		//---- button181 ----
		button181.setText("text"); //$NON-NLS-1$
		contentPane.add(button181, CC.xy(28, 28));

		//---- button182 ----
		button182.setText("text"); //$NON-NLS-1$
		contentPane.add(button182, CC.xy(30, 28));

		//---- button223 ----
		button223.setText("text"); //$NON-NLS-1$
		contentPane.add(button223, CC.xy(32, 28));

		//---- button183 ----
		button183.setText("text"); //$NON-NLS-1$
		contentPane.add(button183, CC.xy(2, 30));

		//---- button184 ----
		button184.setText("text"); //$NON-NLS-1$
		contentPane.add(button184, CC.xy(4, 30));

		//---- button185 ----
		button185.setText("text"); //$NON-NLS-1$
		contentPane.add(button185, CC.xy(6, 30));

		//---- button186 ----
		button186.setText("text"); //$NON-NLS-1$
		contentPane.add(button186, CC.xy(8, 30));

		//---- button187 ----
		button187.setText("text"); //$NON-NLS-1$
		contentPane.add(button187, CC.xy(10, 30));

		//---- button188 ----
		button188.setText("text"); //$NON-NLS-1$
		contentPane.add(button188, CC.xy(12, 30));

		//---- button189 ----
		button189.setText("text"); //$NON-NLS-1$
		contentPane.add(button189, CC.xy(14, 30));

		//---- button190 ----
		button190.setText("text"); //$NON-NLS-1$
		contentPane.add(button190, CC.xy(16, 30));

		//---- button191 ----
		button191.setText("text"); //$NON-NLS-1$
		contentPane.add(button191, CC.xy(18, 30));

		//---- button192 ----
		button192.setText("text"); //$NON-NLS-1$
		contentPane.add(button192, CC.xy(20, 30));

		//---- button193 ----
		button193.setText("text"); //$NON-NLS-1$
		contentPane.add(button193, CC.xy(22, 30));

		//---- button194 ----
		button194.setText("text"); //$NON-NLS-1$
		contentPane.add(button194, CC.xy(24, 30));

		//---- button195 ----
		button195.setText("text"); //$NON-NLS-1$
		contentPane.add(button195, CC.xy(26, 30));

		//---- button196 ----
		button196.setText("text"); //$NON-NLS-1$
		contentPane.add(button196, CC.xy(28, 30));

		//---- button197 ----
		button197.setText("text"); //$NON-NLS-1$
		contentPane.add(button197, CC.xy(30, 30));

		//---- button224 ----
		button224.setText("text"); //$NON-NLS-1$
		contentPane.add(button224, CC.xy(32, 30));

		//---- button198 ----
		button198.setText("text"); //$NON-NLS-1$
		contentPane.add(button198, CC.xy(2, 32));

		//---- button199 ----
		button199.setText("text"); //$NON-NLS-1$
		contentPane.add(button199, CC.xy(4, 32));

		//---- button200 ----
		button200.setText("text"); //$NON-NLS-1$
		contentPane.add(button200, CC.xy(6, 32));

		//---- button201 ----
		button201.setText("text"); //$NON-NLS-1$
		contentPane.add(button201, CC.xy(8, 32));

		//---- button202 ----
		button202.setText("text"); //$NON-NLS-1$
		contentPane.add(button202, CC.xy(10, 32));

		//---- button203 ----
		button203.setText("text"); //$NON-NLS-1$
		contentPane.add(button203, CC.xy(12, 32));

		//---- button204 ----
		button204.setText("text"); //$NON-NLS-1$
		contentPane.add(button204, CC.xy(14, 32));

		//---- button205 ----
		button205.setText("text"); //$NON-NLS-1$
		contentPane.add(button205, CC.xy(16, 32));

		//---- button206 ----
		button206.setText("text"); //$NON-NLS-1$
		contentPane.add(button206, CC.xy(18, 32));

		//---- button207 ----
		button207.setText("text"); //$NON-NLS-1$
		contentPane.add(button207, CC.xy(20, 32));

		//---- button208 ----
		button208.setText("text"); //$NON-NLS-1$
		contentPane.add(button208, CC.xy(22, 32));

		//---- button209 ----
		button209.setText("text"); //$NON-NLS-1$
		contentPane.add(button209, CC.xy(24, 32));

		//---- button210 ----
		button210.setText("text"); //$NON-NLS-1$
		contentPane.add(button210, CC.xy(26, 32));

		//---- button211 ----
		button211.setText("text"); //$NON-NLS-1$
		contentPane.add(button211, CC.xy(28, 32));

		//---- button212 ----
		button212.setText("text"); //$NON-NLS-1$
		contentPane.add(button212, CC.xy(30, 32));

		//---- button225 ----
		button225.setText("text"); //$NON-NLS-1$
		contentPane.add(button225, CC.xy(32, 32));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JButton btnOn;
	private JButton btnOff;
	private JButton button2;
	private JButton button3;
	private JButton button4;
	private JButton button5;
	private JButton button6;
	private JButton button7;
	private JButton button8;
	private JButton button9;
	private JButton button10;
	private JButton button11;
	private JButton button12;
	private JButton button13;
	private JButton button14;
	private JButton button15;
	private JButton button16;
	private JButton button17;
	private JButton button18;
	private JButton button19;
	private JButton button20;
	private JButton button21;
	private JButton button22;
	private JButton button23;
	private JButton button24;
	private JButton button25;
	private JButton button26;
	private JButton button27;
	private JButton button28;
	private JButton button29;
	private JButton button30;
	private JButton button31;
	private JButton button32;
	private JButton button213;
	private JButton button33;
	private JButton button34;
	private JButton button35;
	private JButton button36;
	private JButton button37;
	private JButton button38;
	private JButton button39;
	private JButton button40;
	private JButton button41;
	private JButton button42;
	private JButton button43;
	private JButton button44;
	private JButton button45;
	private JButton button46;
	private JButton button47;
	private JButton button214;
	private JButton button48;
	private JButton button49;
	private JButton button50;
	private JButton button51;
	private JButton button52;
	private JButton button53;
	private JButton button54;
	private JButton button55;
	private JButton button56;
	private JButton button57;
	private JButton button58;
	private JButton button59;
	private JButton button60;
	private JButton button61;
	private JButton button62;
	private JButton button215;
	private JButton button63;
	private JButton button64;
	private JButton button65;
	private JButton button66;
	private JButton button67;
	private JButton button68;
	private JButton button69;
	private JButton button70;
	private JButton button71;
	private JButton button72;
	private JButton button73;
	private JButton button74;
	private JButton button75;
	private JButton button76;
	private JButton button77;
	private JButton button216;
	private JButton button78;
	private JButton button79;
	private JButton button80;
	private JButton button81;
	private JButton button82;
	private JButton button83;
	private JButton button84;
	private JButton button85;
	private JButton button86;
	private JButton button87;
	private JButton button88;
	private JButton button89;
	private JButton button90;
	private JButton button91;
	private JButton button92;
	private JButton button217;
	private JButton button93;
	private JButton button94;
	private JButton button95;
	private JButton button96;
	private JButton button97;
	private JButton button98;
	private JButton button99;
	private JButton button100;
	private JButton button101;
	private JButton button102;
	private JButton button103;
	private JButton button104;
	private JButton button105;
	private JButton button106;
	private JButton button107;
	private JButton button218;
	private JButton button108;
	private JButton button109;
	private JButton button110;
	private JButton button111;
	private JButton button112;
	private JButton button113;
	private JButton button114;
	private JButton button115;
	private JButton button116;
	private JButton button117;
	private JButton button118;
	private JButton button119;
	private JButton button120;
	private JButton button121;
	private JButton button122;
	private JButton button219;
	private JButton button123;
	private JButton button124;
	private JButton button125;
	private JButton button126;
	private JButton button127;
	private JButton button128;
	private JButton button129;
	private JButton button130;
	private JButton button131;
	private JButton button132;
	private JButton button133;
	private JButton button134;
	private JButton button135;
	private JButton button136;
	private JButton button137;
	private JButton button220;
	private JButton button138;
	private JButton button139;
	private JButton button140;
	private JButton button141;
	private JButton button142;
	private JButton button143;
	private JButton button144;
	private JButton button145;
	private JButton button146;
	private JButton button147;
	private JButton button148;
	private JButton button149;
	private JButton button150;
	private JButton button151;
	private JButton button152;
	private JButton button221;
	private JButton button153;
	private JButton button154;
	private JButton button155;
	private JButton button156;
	private JButton button157;
	private JButton button158;
	private JButton button159;
	private JButton button160;
	private JButton button161;
	private JButton button162;
	private JButton button163;
	private JButton button164;
	private JButton button165;
	private JButton button166;
	private JButton button167;
	private JButton button222;
	private JButton button168;
	private JButton button169;
	private JButton button170;
	private JButton button171;
	private JButton button172;
	private JButton button173;
	private JButton button174;
	private JButton button175;
	private JButton button176;
	private JButton button177;
	private JButton button178;
	private JButton button179;
	private JButton button180;
	private JButton button181;
	private JButton button182;
	private JButton button223;
	private JButton button183;
	private JButton button184;
	private JButton button185;
	private JButton button186;
	private JButton button187;
	private JButton button188;
	private JButton button189;
	private JButton button190;
	private JButton button191;
	private JButton button192;
	private JButton button193;
	private JButton button194;
	private JButton button195;
	private JButton button196;
	private JButton button197;
	private JButton button224;
	private JButton button198;
	private JButton button199;
	private JButton button200;
	private JButton button201;
	private JButton button202;
	private JButton button203;
	private JButton button204;
	private JButton button205;
	private JButton button206;
	private JButton button207;
	private JButton button208;
	private JButton button209;
	private JButton button210;
	private JButton button211;
	private JButton button212;
	private JButton button225;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
