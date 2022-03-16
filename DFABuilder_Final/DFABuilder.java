import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.awt.geom.AffineTransform;
import java.awt.geom.AffineTransform.*;
public class DFABuilder extends JFrame {
  
         private ArrayList<State> list = new ArrayList<State>();
         private ArrayList<State> finalStates = new ArrayList<State>();
         private ArrayList<Transition> transitions = new ArrayList<Transition>();   
         private LinkedQueue dfa = new LinkedQueue();
         private String dfaString = "";
         private String doneString = "";
         private State startV = null;
         private State initState = null;
         private State current = null;
         private boolean isLineOn = false;
         private boolean passThru = false;
         private boolean accepted = false;
         private boolean denied = false;
         private boolean reset = false;
         private boolean quit = false;
         private int endOfLineX, endOfLineY, count, clicks;
         private int count2, count3, count4;
         private JTextField input = new JTextField("", 20);
         private JButton jbtReset = new JButton("Reset");
         private JButton jbtRun = new JButton("Run");
         private JButton jbtStep = new JButton("Step");
         private JButton jbtTrace = new JButton("Trace");
         private JLabel jlblStatus = new JLabel("Status:");
         private JLabel jlblDone = new JLabel("Done:");
         private JLabel jlblToDo = new JLabel("Still to do:");
        
        public static void main(String[] args)
        {
            DFABuilder frame = new DFABuilder();
            frame.setTitle("DFA Bulider");
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(3);
            frame.setVisible(true);
        }
       
        public DFABuilder() {
          
            
           JPanel p = new JPanel(new GridLayout(0,5));
            
           jbtRun.addActionListener(new RunButtonListener());
           jbtReset.addActionListener(new ResetButtonListener());
            
           jbtStep.addActionListener(new StepButtonListener());
           jbtTrace.addActionListener(new TraceButtonListener());
            
            p.add(jbtReset, BorderLayout.EAST);
            p.add(input);
            p.add(jbtRun, BorderLayout.WEST);
            p.add(jbtTrace);
            p.add(jbtStep);

            p.add(jlblStatus);
            p.add(jlblDone);
            p.add(jlblToDo);
            add(p, "North");
            add(new GraphPanel(), "Center");
        }
        class ResetButtonListener implements ActionListener
        {
          public void actionPerformed(ActionEvent e)
          { 
            jlblToDo.setText("Still to do:");
            input.setText("");
            isLineOn = false; passThru = false; accepted = false; denied = false; quit = false;
            current = initState;
            reset = true;
            count = 0; count2 = 0; count3 = 0; count4 = 0;
            dfaString = ""; doneString = "";
            jlblDone.setText("Done:");
            jlblStatus.setText("Status:");
            dfa = new LinkedQueue();
            repaint(); 
          }
        }
        class RunButtonListener implements ActionListener
        {
          public void actionPerformed(ActionEvent e)
          {
            dfaString = input.getText();
            while(count3 < dfaString.length())
            {
              dfa.enqueue(dfaString.charAt(count3));
              count3++;
            }
            for(int i = 0; i < list.size(); i++)
            {
              State temp = list.get(i);
              if(temp == initState) 
              {
                current = temp;
              }
            }
            while(count4 < dfaString.length() && !quit)
            {
              Transition t = findTrans(current, dfaString.charAt(count4));
              if(t != null)
              {
                    current = t.v;
                    dfa.dequeue();
                    count4++;
              }
              if(t == null && !accepted)
              {
                count4 = dfaString.length();
                quit = true;
                jlblStatus.setText("Status: REJECTED!");
              }
            }
            if(finalStates.contains(current) && count4 == dfaString.length()) {
                    jlblStatus.setText("Status: ACCEPTED!");
                    quit = true;
            }
            else
              jlblStatus.setText("Status: REJECTED!");
          }
        }
        class TraceButtonListener implements ActionListener
        {
          public void actionPerformed(ActionEvent e)
          {
            dfaString = input.getText();
            while(count < dfaString.length())
            {
              dfa.enqueue(dfaString.charAt(count));
              count++;
            }
            for(int i = 0; i < list.size(); i++)
            {
              State temp = list.get(i);
              if(temp == initState) 
              {
                current = temp;
                passThru = true;
                repaint();
              }
            }
            jlblToDo.setText("Still to do:            " + dfaString);
          }
        }
        class StepButtonListener implements ActionListener
        {
          public void actionPerformed(ActionEvent e)
          {   
              if(count2 < dfaString.length())
              {
                Transition t = findTrans(current, dfaString.charAt(count2));
                doneString += dfa.front.getInfo();
                if(t != null)
                {
                  count2++;
                  current = t.v;
                  passThru = true;
                  repaint();
                  dfa.dequeue();
                  jlblToDo.setText("Still to do:            " + dfa);
                  jlblDone.setText("Done:                 " + doneString);
                }
                if(finalStates.contains(current) && count2 == dfaString.length()) {
                    accepted = true;
                    jlblStatus.setText("Status: ACCEPTED!");
                }
                if(t == null) {
                  denied = true;
                  jlblStatus.setText("Status: REJECTED!");
                  repaint();
                }
              }repaint();
           }
        } 
        boolean isTooCloseToState(Point p) {
          for (int i = 0; i < list.size(); i++)
            if (State.getDistance(list.get(i).getX(), list.get(i).getY(), p.x, p.y) <= 2 * State.RADIUS + 20)
            return true;
          
          return false;
        }
        Transition findTrans(State state, char s)
        {
          for(int i = 0; i < transitions.size(); i++)
          {
            Transition temp = transitions.get(i);
              if(temp.u == state && temp.sym == s)
                 return temp;
          }
          return null;
        }
        State getContainingState(Point p) {
          for (int i = 0; i < list.size(); i++)
            if (list.get(i).contains(p))
            return list.get(i);
          
          return null;
        }
        
        void removeAdjacentTransitions(State state) {
          for (int i = 0; i < transitions.size(); i++)
            if (transitions.get(i).u.equals(state) || transitions.get(i).v.equals(state)) {
            transitions.remove(i--); 
          }
        }
        class GraphPanel extends JPanel {
       
        GraphPanel() {
 
          addMouseListener(new MouseAdapter() {
            
            // Why no @Override's here? -- Interface, not abstract class
            
            public void mouseClicked(MouseEvent e) {
              if (e.getButton() == MouseEvent.BUTTON1) {
                // Add a state
                if (!isTooCloseToState(e.getPoint())) {
                  list.add(new State(e.getPoint()));
                  repaint();
                }
              }
              else if (e.getButton() == MouseEvent.BUTTON3) {
                // remove a state
                State c = getContainingState(e.getPoint());
                if (c != null) {
                  list.remove(c);
                  removeAdjacentTransitions(c);
                  repaint();
                }
              }       
            }
            
            public void mousePressed(MouseEvent e) {
              State c = getContainingState(e.getPoint());
              if (c != null) {
                startV = c;        
                endOfLineX = e.getX();
                endOfLineY = e.getY();
                isLineOn = true;      
                repaint();
              }
              if (e.getButton() == MouseEvent.BUTTON1)
              {
                if (e.isAltDown())
                {
                  initState = c;
                  if (finalStates.contains(c)) 
                    finalStates.remove(c);
                }
                if (e.isShiftDown() && c != initState)
                {
                  if (finalStates.contains(c)) 
                    finalStates.remove(c);
                  else 
                    finalStates.add(c);
                }
              }
              else if (e.getButton() == MouseEvent.BUTTON3) {
                // remove a state
                c = getContainingState(e.getPoint());
                if (c != null) {
                  list.remove(c);
                  removeAdjacentTransitions(c);
                  repaint();
                }
              }    
            }
            
            public void mouseReleased(MouseEvent e) {
              State c = getContainingState(e.getPoint());
              if (isLineOn && c != null && !c.equals(startV)) {
                String lineChar = JOptionPane.showInputDialog("Enter character");
                char tranC = lineChar.charAt(0);
                Transition temp = new Transition(startV, c, tranC); 
                transitions.add(temp);
              }
              if (isLineOn && c != null && c.equals(startV) && !e.isAltDown() && !e.isShiftDown()) {
                String lineChar = JOptionPane.showInputDialog("Enter character");
                char selfC = lineChar.charAt(0);
                transitions.add(new Transition(startV, c, selfC));
              }
              isLineOn = false;
              repaint();
            }
          });
          // end addMouseListener(new MouseAdapter())
          
          
          addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
              if (e.isControlDown()) {
                isLineOn = false;
                State c = getContainingState(e.getPoint());
                if (c != null) {
                  c.setX(e.getX());
                  c.setY(e.getY());
                  repaint();
                }    
              }
              
              else if (isLineOn) {
                endOfLineX = e.getX();
                endOfLineY = e.getY();
                repaint();
              }
            }
          });
        }
        
        void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
          int ARR_SIZE = 8;
          Graphics2D g = (Graphics2D) g1.create();
          
          
          double dx = x2 - x1, dy = y2 - y1;
          double angle = Math.atan2(dy, dx);
          int len = (int) Math.sqrt(dx*dx + dy*dy);
          AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
          at.concatenate(AffineTransform.getRotateInstance(angle));
          g.transform(at);
          
          // Draw horizontal arrow starting in (0, 0)
          g.drawLine(0, 0, len, 0);
          g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                        new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
        }
     
            
        @Override
        protected void paintComponent(Graphics g) {
          super.paintComponent(g);
          for (int i = 0; i < list.size(); i++) {
            if (isLineOn) {
            g.drawLine(startV.getX(), startV.getY(), endOfLineX, endOfLineY);
            }
            if (list.get(i) == initState)
            {
              Polygon triangle = new Polygon();
              int x = list.get(i).getX() - 20;
              int y = list.get(i).getY();
              triangle.addPoint(x, y);
              triangle.addPoint(x - 10, y - 12);
              triangle.addPoint(x - 10, y + 12);
              g.drawPolygon(triangle);
            }
            if (reset) {
              g.setColor(Color.ORANGE);
            }
            if (list.get(i) == current)
            {
              if (passThru && !accepted) {
                g.setColor(Color.PINK);
              }
              if (accepted) {
                g.setColor(Color.GREEN);
              }
              if (denied) {
                g.setColor(Color.RED);
              }
            }
            else {
             g.setColor(Color.ORANGE);
            }
           g.fillOval(list.get(i).getX() - State.RADIUS, list.get(i).getY() - State.RADIUS, 2 * State.RADIUS, 2 * State.RADIUS);
           g.setColor(Color.DARK_GRAY);
           g.drawOval(list.get(i).getX() - State.RADIUS, list.get(i).getY() - State.RADIUS, 2 * State.RADIUS, 2 * State.RADIUS);
           if (finalStates.contains(list.get(i))) {
               g.drawOval((list.get(i)).getX() - 20 + 5, (list.get(i)).getY() - 20 + 5, 30, 30);
           }
          }
             for (int i = 0; i < transitions.size(); i++) {
               if (transitions.get(i).u.equals(transitions.get(i).v)) {
                 int x = transitions.get(i).u.getX();
                 int y = transitions.get(i).u.getY();
                 g.drawArc(x - State.RADIUS / 2, (int)(y - State.RADIUS * Math.sqrt(3)), State.RADIUS, (int)(State.RADIUS * Math.sqrt(3)), 0, 180);
                 g.fillPolygon(new int[] { x + State.RADIUS / 2, x + State.RADIUS / 2 - 5, x + State.RADIUS / 2 + 5 }, 
                               new int[] { (int)(y - State.RADIUS * Math.sqrt(3) / 2), (int)(y - State.RADIUS * Math.sqrt(3) / 2) - 8, 
                                 (int)(y - State.RADIUS * Math.sqrt(3) / 2) - 8 }, 3);
                 g.drawString(transitions.get(i).sym + "  ", x + State.RADIUS / 2,(int)(y - State.RADIUS * Math.sqrt(3) / 2) - 10);
               }
               else {
                 int x1 = transitions.get(i).u.getX();
                 int y1 = transitions.get(i).u.getY();
                 int x2 = transitions.get(i).v.getX();
                 int y2 = transitions.get(i).v.getY();
                 
                 double distance = State.getDistance(x1, y1, x2, y2);
                 double newLength = State.RADIUS / distance;  double newLength2 = (distance - State.RADIUS) / distance;
                 
                 int x3 = x1 + (int)(newLength * (x2 - x1));
                 int y3 = y1 + (int)(newLength * (y2 - y1));
                 int x4 = x1 + (int)(newLength2 * (x2 - x1));
                 int y4 = y1 + (int)(newLength2 * (y2 - y1));
                 
                 drawArrow(g, x3, y3, x4, y4);
                 g.drawLine(x3, y3, x4, y4);
                 g.drawString(transitions.get(i).sym + "", x1 + (int)(0.33 * (x2 - x1)), y1 + (int)(0.33 * (y2 - y1)));
               }
          }
        }
    }
}

      
