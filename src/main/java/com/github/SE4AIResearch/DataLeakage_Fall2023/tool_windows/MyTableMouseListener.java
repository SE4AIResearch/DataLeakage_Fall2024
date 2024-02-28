package com.github.SE4AIResearch.DataLeakage_Fall2023.tool_windows;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;

public class MyTableMouseListener extends MouseInputAdapter {
   private JBTable table;
   private Project project;
//   private VirtualFile file;

   public MyTableMouseListener(JBTable table, Project project) {
      this.table = table;
      this.project = project;
//      this.file = project.getProjectFile();
   }

   @Override
   public void mouseClicked(MouseEvent e) {
      int row = table.rowAtPoint(e.getPoint());
      int column = table.columnAtPoint(e.getPoint());
      DefaultTableModel model = (DefaultTableModel) table.getModel();
      String cellValue = (String) model.getValueAt(row, column);
      int cellValueInt = Integer.parseInt(cellValue); // TODO this can result in NumberFormatException must be fixed

      if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
         if (row >= 0 && column >= 0) {
            // TODO check that its actually the right column and that the input is valid before attempting to move
            moveToLine(cellValueInt);
         }
      }
   }

   private void moveToLine(int lineNumber) {
      if (project != null) {
         Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
         if (editor != null) {
            editor.getCaretModel().moveToLogicalPosition(editor.offsetToLogicalPosition(editor.getDocument().getLineStartOffset(lineNumber - 1)));
            editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
         }
      }
   }
}
