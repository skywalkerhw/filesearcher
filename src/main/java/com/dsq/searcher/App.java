package com.dsq.searcher;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

public class App extends JFrame
{
    private JTextField filePathText;
    private JTextField keysText;
    private JTable resultTable;

    private JScrollPane scrollPane;
    
    private JLabel statusLabel;
    
    private JCheckBox onlyNameCheckbox;
    
    private JCheckBox updateIndexCheck;
    public static void main(String[] args)
    {
        new App();
    }

    public App()
    {
        String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        try
        {
            UIManager.setLookAndFeel(lookAndFeel);
        }
        catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (UnsupportedLookAndFeelException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JFrame frame = new JFrame();
        setTitle("文件搜索");
        getContentPane().setLayout(null);

        filePathText = new JTextField();
        filePathText.setEditable(false);
        filePathText.setToolTipText("文件根目录");
        filePathText.setBounds(178, 52, 300, 27);
        getContentPane().add(filePathText);
        filePathText.setColumns(60);

        JButton fileTextBtn = new JButton("选择目录");
        fileTextBtn.setBounds(39, 51, 124, 29);
        getContentPane().add(fileTextBtn);
        fileTextBtn.setActionCommand("selFile");
        fileTextBtn.addActionListener(new ButtonClickListener());

        keysText = new JTextField();
        keysText.setBounds(178, 115, 300, 27);
        getContentPane().add(keysText);
        keysText.setColumns(10);

        JLabel lblNewLabel = new JLabel("输入关键字");
        lblNewLabel.setBounds(39, 118, 103, 21);
        getContentPane().add(lblNewLabel);

        JButton searchBtn = new JButton("搜索");
        searchBtn.setBounds(504, 114, 124, 29);
        getContentPane().add(searchBtn);

        searchBtn.setActionCommand("search");
        searchBtn.addActionListener(new ButtonClickListener());

        

        resultTable = new JTable(new Object[][]{}, new String[] { "文件名"});
        resultTable.setRowHeight(30);
        resultTable.getTableHeader().setVisible(false);
        
        
        scrollPane = new JScrollPane(resultTable);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setToolTipText("搜索结果");
        scrollPane.setBounds(39, 255, 925, 296);
        getContentPane().add(scrollPane);
        
        JLabel lblNewLabel_1 = new JLabel("搜索结果");
        lblNewLabel_1.setBounds(39, 219, 81, 21);
        getContentPane().add(lblNewLabel_1);

        onlyNameCheckbox = new JCheckBox("只搜索文件名");
        onlyNameCheckbox.setBounds(639, 114, 163, 29);
        getContentPane().add(onlyNameCheckbox);
        
        
        
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        statusLabel.setBounds(39, 183, 191, 21);
        getContentPane().add(statusLabel);
        
        updateIndexCheck = new JCheckBox("不更新索引");
        updateIndexCheck.setBounds(809, 114, 149, 29);
        getContentPane().add(updateIndexCheck);
        

        this.setSize(new Dimension(1024, 800));
        this.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String command = e.getActionCommand();
            //选择目录
            if (command.equals("selFile"))
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setCurrentDirectory(new File("C:\\"));  
                int retVal = fileChooser.showOpenDialog(getContentPane());
                if (retVal == JFileChooser.APPROVE_OPTION)
                {
                    filePathText.setText(fileChooser.getSelectedFile().getPath());

                }

            }
            // 搜索
            else if (command.equals("search"))
            {
                String key = keysText.getText();
                String dir = filePathText.getText();
                
                if(dir == null || "".equals(dir))
                {
                    statusLabel.setText("请选择查询目录");
                    getContentPane().revalidate();
                    return ;
                }
                if(key == null || "".equals(key))
                {
                    statusLabel.setText("请输入查询关键字");
                    getContentPane().revalidate();
                    return;
                    
                }
                boolean onlyName = onlyNameCheckbox.isSelected();
                if(!updateIndexCheck.isSelected())
                {
                    statusLabel.setText("正在更新索引");
                    getContentPane().revalidate();
                    Indexer.updateIndex(dir,onlyName);
                }
                statusLabel.setText("正在查询");
                getContentPane().revalidate();
                
                List<FileData> resultList = Searcher.searchKeys(key, dir,onlyName);
                  
                resultTable.setModel(new DefaultTableModel(listToArray(resultList), new String[] { "文件名"}));
                statusLabel.setText("查询成功"); 

                getContentPane().revalidate();

            }

        }
    }


    private static String[][] listToArray(List<FileData> rList)
    {
        String[][] retArray = new String[rList.size()][1];

        for (int i = 0; i < rList.size(); i++)
        {
            retArray[i][0] = rList.get(i).getFilePath();

        }
        return retArray;

    }
}
