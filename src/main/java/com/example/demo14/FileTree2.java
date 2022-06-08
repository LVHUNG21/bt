package com.example.demo14;


import javafx.event.EventHandler;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;


public class FileTree2
        extends JFrame {
    public static final ImageIcon ICON_COMPUTER =
            new ImageIcon("C:\\trong\\computer.png");
    public static final ImageIcon ICON_DISK =
            new ImageIcon("disk.gif");
    public static final ImageIcon ICON_FOLDER =
            new ImageIcon("folder.gif");
    public static final ImageIcon ICON_EXPANDEDFOLDER =
            new ImageIcon("expandedfolder.gif");

    protected JTree m_tree;
    protected DefaultTreeModel m_model;
    protected JTextField m_display;
    protected JTextPane jTextPane = new JTextPane();
    // NEW


    protected JPopupMenu m_popup;
    protected Action m_action;
    protected TreePath m_clickedPath;

    public FileTree2() {
        super("Directories Tree [Popup Menus]");
        setSize(800, 500);

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(
                new IconData(ICON_COMPUTER, null, "Computer"));

        DefaultMutableTreeNode node;
        File[] roots = File.listRoots();
        for (int k = 0; k < roots.length; k++) {
            node = new DefaultMutableTreeNode(new IconData(ICON_DISK,
                    null, new FileNode(roots[k])));
            top.add(node);
            node.add(new DefaultMutableTreeNode(new Boolean(true)));
        }

        m_model = new DefaultTreeModel(top);
        m_tree = new JTree(m_model);

//        m_tree.putClientProperty("JTree.lineStyle", "Angled");

//

        m_tree.addTreeExpansionListener(new
                DirExpansionListener());

        m_tree.addTreeSelectionListener(new
                DirSelectionListener());

        m_tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
//        m_tree.setShowsRootHandles(true);
        m_tree.setEditable(false);
        JMenu m1 = new JMenu("File");
        // Create menu items
        JMenuItem mi1 = new JMenuItem("New");
        JMenuItem mi2 = new JMenuItem("Open");
        JMenuItem mi3 = new JMenuItem("Save");
        JMenuItem mi9 = new JMenuItem("Print");

        // Add action listener
        m1.add(mi1);
        m1.add(mi2);
        m1.add(mi3);
        m1.add(mi9);
        mi1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        mi2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser j = new JFileChooser("f:");

                // Invoke the showsOpenDialog function to show the save dialog
                int r = j.showOpenDialog(null);

                // If the user selects a file
                if (r == JFileChooser.APPROVE_OPTION) {
                    // Set the label to the path of the selected directory
                    File fi = new File(j.getSelectedFile().getAbsolutePath());

                    try {
                        // String
                        String s1 = "", sl = "";

                        // File reader
                        FileReader fr = new FileReader(fi);

                        // Buffered reader
                        BufferedReader br = new BufferedReader(fr);

                        // Initialize sl
                        sl = br.readLine();

                        // Take the input from the file
                        while ((s1 = br.readLine()) != null) {
                            sl = sl + "\n" + s1;
                        }

                        // Set the text
                        jTextPane.setText(sl);
                    }
                    catch (Exception evt) {
                        JOptionPane.showMessageDialog(FileTree2.this, evt.getMessage());
                    }
                }
                // If the user cancelled the operation
                else
                    JOptionPane.showMessageDialog(FileTree2.this, "the user cancelled the operation");
            }

        });
        mi3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser j = new JFileChooser("f:");

                // Invoke the showsSaveDialog function to show the save dialog
                int r = j.showSaveDialog(null);

                if (r == JFileChooser.APPROVE_OPTION) {

                    // Set the label to the path of the selected directory
                    File fi = new File(j.getSelectedFile().getAbsolutePath());

                    try {
                        // Create a file writer
                        FileWriter wr = new FileWriter(fi, false);

                        // Create buffered writer to write
                        BufferedWriter w = new BufferedWriter(wr);

                        // Write
                        w.write(jTextPane.getText());

                        w.flush();
                        w.close();
                    }
                    catch (Exception evt) {
                        JOptionPane.showMessageDialog(FileTree2.this, evt.getMessage());
                    }
                }
                // If the user cancelled the operation
                else
                    JOptionPane.showMessageDialog(FileTree2.this, "the user cancelled the operation");
            }

        });
        mi9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // print the file
                    jTextPane.print();
                }
                catch (Exception evt) {
                    JOptionPane.showMessageDialog(FileTree2.this, evt.getMessage());
                }
            }
        });

        m_display = new JTextField();
        m_display.setEditable(false);
        JMenuBar mb = new JMenuBar();

        setJMenuBar(mb);
        mb.add(m1);
        getContentPane().add(m_display, BorderLayout.NORTH);
        m_tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
//                    TreePath path = m_tree.getPathForLocation(e.getX(), e.getY());
                    TreePath path = m_tree.getPathForLocation(e.getX(), e.getY());
                   String path2 = removeCharAt(removeCharAt(path.toString(), path.toString().length() - 1), 0);
                    path2 = path2.replace("\\", "");
                    path2 = path2.replace(", ", "\\");
                    path2 = path2.replace("Computer\\", "");
                    DefaultMutableTreeNode node = getTreeNode(path);
                    Rectangle pathBounds = m_tree.getUI().getPathBounds(m_tree, path);
                    if (pathBounds != null && pathBounds.contains(e.getX(), e.getY())) {
                        String pathfinal=path2;
                        JPopupMenu menu = new JPopupMenu();
                        menu.add(new JMenuItem("Test"));
                        JMenuItem delete = new JMenuItem("DELETE");
                        JMenuItem add = new JMenuItem("Add node");
                        add.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent event) {
                                DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) m_tree
                                        .getLastSelectedPathComponent();

                                if (selNode == null) {
                                    return;
                                }
                                String m = JOptionPane.showInputDialog("Mời bạn nhập tên");
                                File file4=new File(pathfinal+"\\"+m);
                                if (!file4.exists()){
                                    file4.mkdirs();
                                }
                                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(m);
                                m_model.insertNodeInto(newNode, selNode, selNode.getChildCount());

                                TreeNode[] nodes = m_model.getPathToRoot(newNode);
                                TreePath path = new TreePath(nodes);
                                m_tree.scrollPathToVisible(path);

                                m_tree.setSelectionPath(path);

                                m_tree.startEditingAtPath(path);
                            }

                        });


                        String finalPath = path2;
                        delete.addActionListener(new ActionListener() {
                            String finalPath1= finalPath;

                            public void actionPerformed(ActionEvent e) {

                                DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) m_tree
                                        .getLastSelectedPathComponent();
//                                System.out.println(removeCharAt(path.toString(),0));
                                final JLabel label = new JLabel();
                                try {
                                    int result = JOptionPane.showConfirmDialog(FileTree2.this,
                                            "Bạn có chắc muốn xoa khong",
                                            "Xác nhận",
                                            JOptionPane.YES_NO_OPTION,
                                            JOptionPane.QUESTION_MESSAGE);
                                    if (result == JOptionPane.YES_OPTION) {
                                        label.setText("Bạn chọn: Yes");
                                        try {
                                            Files.deleteIfExists(
                                                    Paths.get(finalPath1));

                                        } catch (IOException ex) {
                                            JOptionPane.showMessageDialog(FileTree2.this,
                                                    "Xin lỗi file này chỉ xóa được trên Jtree không thể xóa trên hệ thống",
                                                    " Hưng thông báo  ", JOptionPane.INFORMATION_MESSAGE);
                                        }
                                        SwingUtilities.updateComponentTreeUI(m_tree);
                                    } else if (result == JOptionPane.NO_OPTION) {
                                        label.setText("Bạn chọn : No");
                                    } else {
                                        label.setText("Chưa ");
                                    }
                                    if (selNode == null) {
                                        return;
                                    }
                                    MutableTreeNode parent = (MutableTreeNode) (selNode.getParent());
                                    if (parent == null) {
                                        return;
                                    }
                                    MutableTreeNode toBeSelNode = (MutableTreeNode) selNode.getPreviousSibling();
                                    if (toBeSelNode == null) {
                                        toBeSelNode = (MutableTreeNode) selNode.getNextSibling();
                                    }
                                    if (toBeSelNode == null) {
                                        toBeSelNode = parent;
                                    }
                                    TreeNode[] nodes = m_model.getPathToRoot(toBeSelNode);
                                    TreePath path = new TreePath(nodes);
                                    m_tree.scrollPathToVisible(path);
                                    m_tree.setSelectionPath(path);
                                    m_model.removeNodeFromParent(selNode);
                                } catch (Exception exception) {
                                    JOptionPane.showMessageDialog(FileTree2.this,
                                            "Xin lỗi file này chỉ xóa được trên Jtree không thể xóa trên hệ thống",
                                            " Hưng thông báo  ", JOptionPane.INFORMATION_MESSAGE);
                                }
//                                m_tree.updateUI();
                            }
                        });
                        menu.add(delete);
                        menu.add(add);
                        menu.show(m_tree, pathBounds.x, pathBounds.y + pathBounds.height);
                        System.out.println(path2);
                    }
                }
            }
        });



        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, new JScrollPane(
                m_tree), new JScrollPane(jTextPane));
        getContentPane().add(splitPane);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);

        m_tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent event) {

                TreePath file = m_tree.getSelectionPath();
                String path2 = removeCharAt(removeCharAt(file.toString(), file.toString().length() - 1), 0);
                path2 = path2.replace("\\", "");
                path2 = path2.replace(", ", "\\");
                path2 = path2.replace("Computer\\", "");
//                System.out.println(path2);
                try {
                    jTextPane.setText(getFileDetails(path2));
                    if (path2.endsWith(".png") || path2.endsWith(".jpg") || path2.endsWith("gif")) {

                        jTextPane.insertIcon(new ImageIcon(path2));
                    }

                } catch (IOException e) {
                    System.out.println("chuadocduoclenmanhinh");
                }
            }
        });

        m_tree.removeTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                System.out.println("levanhung");
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {

            }
        });
// NE
//        m_popup = new JPopupMenu();
//        m_action = new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                if (m_clickedPath == null)
//                    return;
//                if (m_tree.isExpanded(m_clickedPath))
//                    m_tree.collapsePath(m_clickedPath);
//                else
//                    m_tree.expandPath(m_clickedPath);
//            }
//        };
//        m_popup.add(m_action);
////        m_popup.addSeparator();
//
//        Action a1 = new AbstractAction("Delete") {
//            public void actionPerformed(ActionEvent e) {
//                m_tree.repaint();
//
//                TreePath file = m_tree.getSelectionPath();
//                String path2 = removeCharAt(removeCharAt(file.toString(), file.toString().length() - 1), 0);
//                path2 = path2.replace("\\", "");
//                path2 = path2.replace(", ", "\\");
//                path2 = path2.replace("Computer\\", "");
//                DefaultMutableTreeNode node = getTreeNode(file);
//                System.out.println(node+ "hungdelete: " + path2);
//                final JLabel label = new JLabel();
//                int result = JOptionPane.showConfirmDialog(FileTree2.this,
//                        "Bạn có chắc muốn xoa khong",
//                        "Xác nhận",
//                        JOptionPane.YES_NO_OPTION,
//                        JOptionPane.QUESTION_MESSAGE);
//                if (result == JOptionPane.YES_OPTION) {
//                    label.setText("Bạn chọn: Yes");
//                    try {
//                        Files.deleteIfExists(
//                                Paths.get(path2));
//                        m_tree.updateUI();
//                            System.out.println("hungyes");
//                            m_tree.updateUI();
//                        SwingUtilities.updateComponentTreeUI(m_tree);
//                        m_model.reload(node);
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                    SwingUtilities.updateComponentTreeUI(m_tree);
//                } else if (result == JOptionPane.NO_OPTION) {
//                    label.setText("Bạn chọn : No");
//                } else {
//                    label.setText("Chưa ");
//                }
//                SwingUtilities.updateComponentTreeUI(m_tree);
//            }
//
//        };
//        m_popup.add(a1);
//
//        Action a2 = new AbstractAction("Rename") {
//            public void actionPerformed(ActionEvent e) {
//                m_tree.repaint();
//
//            }
//        };
//        m_popup.add(a2);
//        m_tree.add(m_popup);
//        m_tree.addMouseListener(new PopupTrigger());
//
//        WindowListener wndCloser = new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        };
//        addWindowListener(wndCloser);
//
//        setVisible(true);
//    }
    }

    private String getFileDetails(String file1) throws IOException {
        File file = new File(file1);

        if (file == null)

            return "khongloadduoc";
        String line = null;
        if (file.getPath().endsWith(".txt")) {
            line = "khongloadduoc";
            try (BufferedReader reader = new BufferedReader(new FileReader(new File(file.getPath())))) {
                String result = "";
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    result += line;
                    result += "\n";
                }
                return result;
            } catch (IOException e) {System.out.println("hungdocfile:Loi doc file");
            }

            return line;
        }
        return line;
    }

    DefaultMutableTreeNode getTreeNode(TreePath path) {
        return (DefaultMutableTreeNode) (path.getLastPathComponent());
    }

    FileNode getFileNode(DefaultMutableTreeNode node) {
        if (node == null)
            return null;
        Object obj = node.getUserObject();
        if (obj instanceof IconData)
            obj = ((IconData) obj).getObject();
        if (obj instanceof FileNode)
            return (FileNode) obj;
        else
            return null;
    }
    public void actionPerformed(ActionEvent e)
    {
        String s = e.getActionCommand();


        if (s.equals("Save")) {
            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser("f:");

            // Invoke the showsSaveDialog function to show the save dialog
            int r = j.showSaveDialog(null);

            if (r == JFileChooser.APPROVE_OPTION) {

                // Set the label to the path of the selected directory
                File fi = new File(j.getSelectedFile().getAbsolutePath());

                try {
                    // Create a file writer
                    FileWriter wr = new FileWriter(fi, false);

                    // Create buffered writer to write
                    BufferedWriter w = new BufferedWriter(wr);

                    // Write
                    w.write(jTextPane.getText());

                    w.flush();
                    w.close();
                }
                catch (Exception evt) {
                    JOptionPane.showMessageDialog(FileTree2.this, evt.getMessage());
                }
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(FileTree2.this, "the user cancelled the operation");
        }

        else if (s.equals("Open")) {
            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser("f:");

            // Invoke the showsOpenDialog function to show the save dialog
            int r = j.showOpenDialog(null);

            // If the user selects a file
            if (r == JFileChooser.APPROVE_OPTION) {
                // Set the label to the path of the selected directory
                File fi = new File(j.getSelectedFile().getAbsolutePath());

                try {
                    // String
                    String s1 = "", sl = "";

                    // File reader
                    FileReader fr = new FileReader(fi);

                    // Buffered reader
                    BufferedReader br = new BufferedReader(fr);

                    // Initialize sl
                    sl = br.readLine();

                    // Take the input from the file
                    while ((s1 = br.readLine()) != null) {
                        sl = sl + "\n" + s1;
                    }

                    // Set the text
                    jTextPane.setText(sl);
                }
                catch (Exception evt) {
                    JOptionPane.showMessageDialog(FileTree2.this, evt.getMessage());
                }
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(FileTree2.this, "the user cancelled the operation");
        }
        else if (s.equals("New")) {
            jTextPane.setText("");
        }
        else if (s.equals("close")) {
            jTextPane.setVisible(false);
        }
    }

    // NEW
    class PopupTrigger extends MouseAdapter {
        public void mouseReleased(MouseEvent e) {
            SwingUtilities.updateComponentTreeUI(m_tree);
            if (e.isPopupTrigger()) {
                int x = e.getX();
                int y = e.getY();
                TreePath path = m_tree.getPathForLocation(x, y);
                if (path != null) {
                    if (m_tree.isExpanded(path))
                        m_action.putValue(Action.NAME, "Collapse");
                    else
                        m_action.putValue(Action.NAME, "Expand");
                    m_popup.show(m_tree, x, y);
                    SwingUtilities.updateComponentTreeUI(m_tree);
                    m_clickedPath = path;
                }
            }
        }
    }
    class DirSelectionListener
            implements TreeSelectionListener
    {
        public void valueChanged(TreeSelectionEvent event)
        {
            DefaultMutableTreeNode node = getTreeNode(
                    event.getPath());
            FileNode fnode = getFileNode(node);
            if (fnode != null)
                m_display.setText(fnode.getFile().
                        getAbsolutePath());
            else
                m_display.setText("");
        }
    }
    // Make sure expansion is threaded and updating the tree model
    // only occurs within the event dispatching thread.
    class DirExpansionListener implements TreeExpansionListener {
        public void treeExpanded(TreeExpansionEvent event) {
            final DefaultMutableTreeNode node = getTreeNode(
                    event.getPath());
            System.out.println("lehungDirExpansion:" + node);
            final FileNode fnode = getFileNode(node);

            Thread runner = new Thread() {
                public void run() {
                    if (fnode != null && fnode.expand(node)) {
                        Runnable runnable = new Runnable() {
                            public void run() {
                                m_model.reload(node);
                            }
                        };
                        SwingUtilities.invokeLater(runnable);
                    }
                }
            };
            runner.start();
        }

        public void treeCollapsed(TreeExpansionEvent event) {
        }
    }


    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    public static void main(String argv[]) {
        new FileTree2();
    }
}


