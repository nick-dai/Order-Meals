import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.filechooser.*;

public class Window extends JFrame {
	ArrayList<Cake> cakes = new ArrayList<Cake>();
	ArrayList<Beverage> beverages = new ArrayList<Beverage>();
	String[] cakeName, beverageName;
	String[] cakeSize = {"6 吋", "8 吋", "10 吋"};
	String[] beverageSize = {"小杯", "中杯", "大杯"};
	String[] typeList = {"蛋糕", "飲料"};

	ArrayList<Record> records = new ArrayList<Record>();
	ArrayList<String> recordList = new ArrayList<String>();
	ArrayList<String> searchList = new ArrayList<String>();
	ArrayList<Integer> searchIndex = new ArrayList<Integer>();

	JComboBox<String> typeBox, entryBox, sizeBox, tBox, eBox, sBox;
	DefaultComboBoxModel<String> cakeModel, beverageModel, cModel, bModel;
	DefaultComboBoxModel<String> cakeSizeModel = new DefaultComboBoxModel<String>(cakeSize);
	DefaultComboBoxModel<String> beverageSizeModel = new DefaultComboBoxModel<String>(beverageSize);
	DefaultComboBoxModel<String> cSizeModel = new DefaultComboBoxModel<String>(cakeSize);
	DefaultComboBoxModel<String> bSizeModel = new DefaultComboBoxModel<String>(beverageSize);
	JTextField priceField = new JTextField(8);
	JTextField countField = new JTextField(8);
	JTextField payField = new JTextField(8);
	JList<String> myRecords, mySearch;

	int pay;
	String myFile = "History.dat";

	public Window() {
		// Set windows' style: Nimbus.
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Set the main window's properties.
		setTitle("蛋糕飲料點餐系統");
		setSize(500, 300);
		setLocationRelativeTo(null); // Center the window based on size data.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.WHITE);

		// Import products' data and selling records.
		fileReader("ProductsList.dat");
		fileReader(myFile);

		// Save products' Chinese names separately and create DefaultComboBoxModel for each.
		cakeName = new String[cakes.size()];
		beverageName = new String[beverages.size()];
		for (int i=0; i<cakes.size(); i++) {
			cakeName[i] = cakes.get(i).getName();
		}
		for (int i=0; i<beverages.size(); i++) {
			beverageName[i] = beverages.get(i).getName();
		}
		cakeModel = new DefaultComboBoxModel<String>(cakeName);
		beverageModel = new DefaultComboBoxModel<String>(beverageName);
		cModel = new DefaultComboBoxModel<String>(cakeName);
		bModel = new DefaultComboBoxModel<String>(beverageName);

		// Create necessary components.
		JButton add = new JButton("新增");
		JButton search = new JButton("查詢");
		JButton delete = new JButton("刪除");
		JButton modify = new JButton("修改");
		add.setToolTipText("將目前記錄儲存至檔案中。");
		search.setToolTipText("搜尋以前的記錄。");
		delete.setToolTipText("刪除上方清單中所選的項目。");
		modify.setToolTipText("修改上方清單中所選的項目。");

		// Set default values.
		typeBox = new JComboBox<String>(typeList);
		entryBox = new JComboBox<String>(cakeModel);
		sizeBox = new JComboBox<String>(cakeSizeModel);
		tBox = new JComboBox<String>(typeList);
		eBox = new JComboBox<String>(cModel);
		sBox = new JComboBox<String>(cSizeModel);
		priceField.setText(String.valueOf(cakes.get(entryBox.getSelectedIndex()).getPrice(sizeBox.getSelectedIndex())));
		priceField.setEditable(false);
		payField.setEditable(false);

		// Create Chinese entries to display.
		for (int i=0; i<records.size(); i++) {
			createChineseEntry(i, false);
		}
		myRecords = new JList<String>(recordList.toArray(new String[recordList.size()]));

		// Register combo-boxes and buttons.
		typeBox.addItemListener(new TypeHandler());
		entryBox.addItemListener(new EntryHandler());
		sizeBox.addItemListener(new EntryHandler());
		add.addActionListener(new SubmitHandler());
		delete.addActionListener(new DeleteHandler(false));
		search.addActionListener(new SearchHandler());
		modify.addActionListener(new ModifyHandler(false));

		// Add components to the window.
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("檔案");
		JMenuItem dat = new JMenuItem("匯入其他記錄檔...");
		JMenuItem currentDat = new JMenuItem("目前記錄檔: " + myFile);
		JMenuItem exit = new JMenuItem("結束");
		currentDat.setEnabled(false);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		dat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("記錄檔 (dat)", "dat");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					myFile = chooser.getSelectedFile().getName();
					records.clear();
					recordList.clear();
					fileReader(myFile);
					for (int i=0; i<records.size(); i++) {
						createChineseEntry(i, false);
					}
					myRecords.setListData(recordList.toArray(new String[recordList.size()]));
					currentDat.setText("目前記錄檔: " + myFile);
					JOptionPane.showMessageDialog(null, "匯入成功!\n目前記錄檔: " + myFile + "。", "成功", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		fileMenu.add(dat);
		fileMenu.addSeparator();
		fileMenu.add(currentDat);
		fileMenu.addSeparator();
		fileMenu.add(exit);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		Container cp = getContentPane();
		cp.setLayout(new GridBagLayout());

		ArrayList<JComponent> myComponents = new ArrayList<JComponent>();
		myComponents.add(new JLabel("種類"));
		myComponents.add(typeBox);
		myComponents.add(new JLabel("品項"));
		myComponents.add(entryBox);
		myComponents.add(new JLabel("尺寸"));
		myComponents.add(sizeBox);
		myComponents.add(new JLabel("價格"));
		myComponents.add(priceField);
		myComponents.add(new JLabel("數量"));
		myComponents.add(countField);
		myComponents.add(new JLabel("總金額"));
		myComponents.add(payField);
		myComponents.add(add);
		myComponents.add(new JScrollPane(myRecords));
		myComponents.add(delete);
		myComponents.add(modify);
		myComponents.add(search);

		int[] x = {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 4, 4, 7, 2};
		int[] y = {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 0, 6, 6, 6};
		int[] w = {1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 2, 7, 3, 3, 2};
		int[] h = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 6, 1, 1, 1};
		int[] wx = {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1};
		JComponent component;
		GridBagConstraints constraint;
		Insets myInset = new Insets(2,2,2,2);
		for (int i=0; i<17; i++) {
			component = myComponents.get(i);
			constraint = new GridBagConstraints();
			constraint.gridx = x[i];
			constraint.gridy = y[i];
			constraint.gridwidth = w[i];
			constraint.gridheight = h[i];
			constraint.weightx = wx[i];
			constraint.weighty = 1;
			constraint.insets = myInset;
			constraint.fill = GridBagConstraints.BOTH;
			constraint.anchor = GridBagConstraints.CENTER;
			cp.add(component, constraint);
		}
	}

	public void setBackground(){
		((JPanel)this.getContentPane()).setOpaque(false);
		ImageIcon img = new ImageIcon("background.jpg");
		JLabel background = new JLabel(img);this.getLayeredPane().add(background, new Integer(Integer.MIN_VALUE));
		background.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());
	}

	public static void main(String[] args) {
		Window frame = new Window();
		frame.setVisible(true);
	}

	private class TypeHandler implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (typeBox.getSelectedIndex() == 0) {
				entryBox.setModel(cakeModel);
				sizeBox.setModel(cakeSizeModel);
				priceField.setText(String.valueOf(cakes.get(entryBox.getSelectedIndex()).getPrice(sizeBox.getSelectedIndex())));
			} else {
				entryBox.setModel(beverageModel);
				sizeBox.setModel(beverageSizeModel);
				priceField.setText(String.valueOf(beverages.get(entryBox.getSelectedIndex()).getPrice(sizeBox.getSelectedIndex())));
			}
		}
	}

	private class EntryHandler implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (typeBox.getSelectedIndex() == 0) priceField.setText(String.valueOf(cakes.get(entryBox.getSelectedIndex()).getPrice(sizeBox.getSelectedIndex())));
			else priceField.setText(String.valueOf(beverages.get(entryBox.getSelectedIndex()).getPrice(sizeBox.getSelectedIndex())));
		}
	}

	private class SubmitHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (countField.getText().equals("")) JOptionPane.showMessageDialog(null, "\"數量\" 欄位不得為空!", "錯誤", JOptionPane.ERROR_MESSAGE);
			else {
				// Open "myFile".
				FileWriter file = null;
				try {
					file = new FileWriter(myFile, true);
				} catch (IOException i) {
					JOptionPane.showMessageDialog(null, "讀取 \"" + myFile + "\" 時發生錯誤!", "錯誤", JOptionPane.ERROR_MESSAGE);
					System.exit(-1);
				}
				PrintWriter print = new PrintWriter(file);
				// Get the current date and time.
				String date = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
				String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
				try {
					// Save current data to "myFile" and add new one in "records".
					int count = Integer.parseInt(countField.getText());
					Record tmpRecord = new Record(date, time, typeBox.getSelectedIndex(), entryBox.getSelectedIndex(), sizeBox.getSelectedIndex(), count);
					String tmpString = date + ", " + time + ", " + typeBox.getSelectedIndex() + ", " + entryBox.getSelectedIndex() + ", " + sizeBox.getSelectedIndex() + ", " + count;
					records.add(tmpRecord);
					print.println(tmpString);
					// Create the Chinese entry and update "myRecords".
					createChineseEntry(records.size()-1, false);
					myRecords.setListData(recordList.toArray(new String[recordList.size()]));
					// Move to bottom in "myRecords".
					int lastIndex = myRecords.getModel().getSize() - 1;
					if (lastIndex >= 0) myRecords.ensureIndexIsVisible(lastIndex);
					countField.setText(""); // Empty "countField".
					payField.setText(String.valueOf(pay)); // Calculate total dollars needed.
				} catch (NumberFormatException n) {
					JOptionPane.showMessageDialog(null, "數字轉換時發生錯誤!\n您輸入的可能不是數字?", "錯誤", JOptionPane.ERROR_MESSAGE);
				}
				// Close myFile.
				print.close();
			}
		}
	}

	private void createChineseEntry(int index, boolean modify) {
		String type, unit, entry, size;
		Record rec = records.get(index);
		if (rec.getType() == 0) {
			entry = cakes.get(rec.getEntry()).getName();
			type = typeList[0];
			size = cakeSize[rec.getSize()];
			unit = "個";
			pay = cakes.get(rec.getEntry()).getPrice(rec.getSize())*rec.getCount();
		} else {
			entry = beverages.get(rec.getEntry()).getName();
			type = typeList[1];
			size = beverageSize[rec.getSize()];
			unit = "杯";
			pay = beverages.get(rec.getEntry()).getPrice(rec.getSize())*rec.getCount();
		}
		if (modify == true) recordList.set(index, rec.getDate() + " " + rec.getTime() + " " + size + type + " " + entry + " " + rec.getCount() + " " + unit + " $" + pay + "。");
		else recordList.add(rec.getDate() + " " + rec.getTime() + " " + size + type + " " + entry + " " + rec.getCount() + " " + unit + " $" + pay + "。");
	}

	private void fileReader(String f) {
		// Read file.
		FileReader file = null;
		try {
			file = new FileReader(f);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "讀取 \"" + f + "\" 時發生錯誤!", "錯誤", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		BufferedReader buffer = new BufferedReader(file);

		// Save data to ArrayList.
		StringTokenizer token;
		String input;
		try {
			input = buffer.readLine();
			if (f.equals("ProductsList.dat")) {
				Cake tmpCake;
				Beverage tmpBeverage;
				int temp;
				while (input != null) {
					token = new StringTokenizer(input, ", ");
					temp = Integer.parseInt(token.nextToken());
					if (temp == 0) {
						tmpCake = new Cake(token.nextToken(), Double.parseDouble(token.nextToken()), Integer.parseInt(token.nextToken()));
						cakes.add(tmpCake);
					} else {
						tmpBeverage = new Beverage(token.nextToken(), Double.parseDouble(token.nextToken()), Integer.parseInt(token.nextToken()));
						beverages.add(tmpBeverage);
					}
					input = buffer.readLine();
				}
			} else {
				Record tmpRecord;
				while (input != null) {
					token = new StringTokenizer(input, ", ");
					tmpRecord = new Record(token.nextToken(), token.nextToken(), Integer.parseInt(token.nextToken()), Integer.parseInt(token.nextToken()), Integer.parseInt(token.nextToken()), Integer.parseInt(token.nextToken()));
					records.add(tmpRecord);
					input = buffer.readLine();
				}
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "讀取 \"" + f + "\" 時發生錯誤!", "錯誤", JOptionPane.ERROR_MESSAGE);
			System.exit(-2);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "數字轉換時發生錯誤!", "錯誤", JOptionPane.ERROR_MESSAGE);
			System.exit(-3);
		}

		// Close file.
		try {
			buffer.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "無法關閉 \"" + f + "\"!", "錯誤", JOptionPane.ERROR_MESSAGE);
		}
	}

	private class DeleteHandler implements ActionListener {
		int index, listIndex;
		boolean search;
		private DeleteHandler(boolean s) {
			search = s;
		}
		public void actionPerformed(ActionEvent e) {
			if (search == true) listIndex = mySearch.getSelectedIndex();
			else listIndex = myRecords.getSelectedIndex();
			if (listIndex == -1) JOptionPane.showMessageDialog(null, "請從清單中選取一個項目!", "錯誤", JOptionPane.ERROR_MESSAGE);
			else {
				// Determine index source.
				if (search == true) {
					index = searchIndex.get(mySearch.getSelectedIndex());
					searchList.remove(mySearch.getSelectedIndex());
					mySearch.setListData(searchList.toArray(new String[searchList.size()]));
				} else index = myRecords.getSelectedIndex();
				// Show a confirmation dialog.
				int result = JOptionPane.showConfirmDialog(null, "確認刪除:\n" + recordList.get(index), "確認", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					recordList.remove(index); // Remove the entry in JList.
					records.remove(index); // Remove data in ArrayList.
					myRecords.setListData(recordList.toArray(new String[recordList.size()]));
					updateFile(myFile);
				}
			}
		}
	}

	private class ModifyHandler implements ActionListener {
		int index, listIndex;
		boolean search;
		private ModifyHandler(boolean s) {
			search = s;
		}
		public void actionPerformed(ActionEvent e) {
			if (search == true) {
				listIndex = mySearch.getSelectedIndex();
				index = searchIndex.get(listIndex);
			} else {
				index = myRecords.getSelectedIndex();
				listIndex = myRecords.getSelectedIndex();
			}
			if (listIndex == -1) JOptionPane.showMessageDialog(null, "請從清單中選取一個項目!", "錯誤", JOptionPane.ERROR_MESSAGE);
			else {
				JPanel window = new JPanel();
				window.setLayout(new GridBagLayout());
				JTextField cField = new JTextField(8);
				ArrayList<JComponent> myComponents = new ArrayList<JComponent>();
				myComponents.add(new JLabel("種類"));
				myComponents.add(tBox);
				myComponents.add(new JLabel("品項"));
				myComponents.add(eBox);
				myComponents.add(new JLabel("尺寸"));
				myComponents.add(sBox);
				myComponents.add(new JLabel("數量"));
				myComponents.add(cField);

				int[] x = {0, 1, 3, 4, 0, 1, 3, 4};
				int[] y = {0, 0, 0, 0, 1, 1, 1, 1};
				int[] w = {1, 2, 1, 2, 1, 2, 1, 2};
				JComponent component;
				GridBagConstraints constraint;
				Insets myInset = new Insets(2,2,2,2);
				for (int i=0; i<8; i++) {
					component = myComponents.get(i);
					constraint = new GridBagConstraints();
					constraint.gridx = x[i];
					constraint.gridy = y[i];
					constraint.gridwidth = w[i];
					constraint.gridheight = 1;
					constraint.weightx = 0;
					constraint.weighty = 0;
					constraint.insets = myInset;
					constraint.fill = GridBagConstraints.BOTH;
					constraint.anchor = GridBagConstraints.CENTER;
					window.add(component, constraint);
				}
				tBox.addItemListener(new smTypeHandler());
				tBox.setSelectedIndex(records.get(index).getType());
				eBox.setSelectedIndex(records.get(index).getEntry());
				sBox.setSelectedIndex(records.get(index).getSize());
				cField.setText(String.valueOf(records.get(index).getCount()));
				int result = JOptionPane.showConfirmDialog(null, window, "修改為", JOptionPane.OK_CANCEL_OPTION);
				
				if (result == JOptionPane.OK_OPTION) {
					if (cField.equals("")) JOptionPane.showMessageDialog(null, "\"數量\" 欄位不得為空!", "錯誤", JOptionPane.ERROR_MESSAGE);
					else {
						try {
							String origStr = recordList.get(index);
							// Update recordList and myRecords.
							String date = records.get(index).getDate();
							String time = records.get(index).getTime();
							Record tmpRecord = new Record(date, time, tBox.getSelectedIndex(), eBox.getSelectedIndex(), sBox.getSelectedIndex(), Integer.parseInt(cField.getText()));
							if (tBox.getSelectedIndex() == records.get(index).getType() && eBox.getSelectedIndex() == records.get(index).getEntry() && sBox.getSelectedIndex() == records.get(index).getSize() && Integer.parseInt(cField.getText()) == records.get(index).getCount()) {
								JOptionPane.showMessageDialog(null, "因為內容相同，因此未修改:\n" + recordList.get(index), "訊息", JOptionPane.INFORMATION_MESSAGE);
							} else {
								records.set(index, tmpRecord);
								createChineseEntry(index, true);
								myRecords.setListData(recordList.toArray(new String[recordList.size()]));
								if (search == true) {
									// Update searchList and mySearch.
									searchList.set(mySearch.getSelectedIndex(), recordList.get(index));
									mySearch.setListData(searchList.toArray(new String[searchList.size()]));
								}
								updateFile(myFile);
								JOptionPane.showMessageDialog(null, "修改成功：\n" + origStr + "\n>>\n" + recordList.get(index), "成功", JOptionPane.INFORMATION_MESSAGE);
							}
						} catch (NumberFormatException n) {
							JOptionPane.showMessageDialog(null, "數字轉換時發生錯誤!\n您輸入的可能不是數字?", "錯誤", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}
	}

	private class SearchHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Build search UI.
			JPanel window = new JPanel();
			window.add(new JLabel("請選擇產品:"));
			window.add(tBox);
			window.add(eBox);
			tBox.setSelectedIndex(0);
			// Register tBox.
			tBox.addItemListener(new smTypeHandler());
			int result = JOptionPane.showConfirmDialog(null, window, "查詢", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				// Save the result to searchList and index.
				searchList.clear();
				searchIndex.clear();
				for (int i=0; i<records.size(); i++) {
					if (records.get(i).getType() == tBox.getSelectedIndex() && eBox.getSelectedIndex() == records.get(i).getEntry()) {
						searchList.add(recordList.get(i));
						searchIndex.add(i);
					}
				}
				if (searchList.size() == 0) JOptionPane.showMessageDialog(null, "找不到任何相符的項目!", "結果", JOptionPane.INFORMATION_MESSAGE);
				else {
					// Build result UI.
					mySearch = new JList<String>(searchList.toArray(new String[searchList.size()]));
					JFrame frame = new JFrame();
					frame.setLayout(new BorderLayout());
					frame.setTitle("查詢結果");
					frame.setSize(500, 300);
					frame.setLocationRelativeTo(null); // Center the window based on size data.
					JPanel buttons = new JPanel();
					JButton del = new JButton("刪除");
					JButton mod = new JButton("修改");
					del.addActionListener(new DeleteHandler(true));
					mod.addActionListener(new ModifyHandler(true));
					buttons.add(del);
					buttons.add(mod);
					frame.add(new JScrollPane(mySearch));
					frame.add(buttons, BorderLayout.SOUTH);
					frame.setVisible(true);
				}
			} else ;
		}
	}

	private class smTypeHandler implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (tBox.getSelectedIndex() == 0) {
				eBox.setModel(cModel);
				sBox.setModel(cSizeModel);
			} else {
				eBox.setModel(bModel);
				sBox.setModel(bSizeModel);
			}
		}
	}

	private void updateFile(String f) {
		FileWriter file = null;
		try {
			file = new FileWriter(f);
		} catch (IOException i) {
			JOptionPane.showMessageDialog(null, "讀取 \"" + f + "\" 時發生錯誤!", "錯誤", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		PrintWriter printer = new PrintWriter(file);
		for (int i=0; i<records.size(); i++) {
			printer.println(records.get(i).getDate() + ", " + records.get(i).getTime() + ", " + String.valueOf(records.get(i).getType()) + ", " + String.valueOf(records.get(i).getEntry()) + ", " + String.valueOf(records.get(i).getSize()) + ", " + String.valueOf(records.get(i).getCount()));
		}
		printer.close();
	}
}