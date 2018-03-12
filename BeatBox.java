import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;


public class BeatBox {


JPanel mainPanel;
ArrayList<JCheckBox> checkboxList;

Sequencer sequencer;
Sequence sequence;
Track track;
JFrame theFrame;
boolean[] checkboxState;


String[] instrumentNames = {"Bass Drums", "Closed HI-Hat", "High TOM", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom","Hi Bongo", "Maracas", "Whistle","Low Conga","Cowbell","vibraslap","Low-mind Tom","High Agono","open HI COgna"};

int [] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};

public static void main (String[]args){
new BeatBox().buildGui();

}

public void buildGui(){
theFrame = new JFrame("cyber beatbox");
theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
BorderLayout layout = new BorderLayout();
JPanel background = new JPanel(layout);
background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
background.setBackground(Color.gray);
checkboxList = new ArrayList<JCheckBox>();

Box buttonBox = new Box(BoxLayout.Y_AXIS);

JButton start = new JButton("start");
start.addActionListener(new MyStartListener());
buttonBox.add(start);

JButton stop = new JButton("stop");
stop.addActionListener(new MyStopListener());
buttonBox.add(stop);

JButton upTempo = new JButton("Tempo Up");
upTempo.addActionListener(new MyUpTempoListener());
buttonBox.add(upTempo);

JButton downTempo = new JButton("Tempo Down");
downTempo.addActionListener(new MyDownTempoListener());
buttonBox.add(downTempo);

JButton save = new JButton("Serialize");
save.addActionListener(new MySendListener());
buttonBox.add(save);

JButton restore = new JButton("restore");
restore.addActionListener(new MyReadListener());
buttonBox.add(restore);

Box nameBox = new Box (BoxLayout.Y_AXIS);
for (int i = 0; i <16; i++){

nameBox.add(new Label(instrumentNames[i]));

}

background.add(BorderLayout.EAST, buttonBox);
background.add(BorderLayout.WEST, nameBox);

theFrame.getContentPane().add(background);

GridLayout grid = new GridLayout(16,16);
grid.setVgap(1);
grid.setHgap(2);
mainPanel = new JPanel(grid);
mainPanel.setBackground(Color.gray);
background.add(BorderLayout.CENTER, mainPanel);

for (int i = 0; i < 256; i++){

JCheckBox c = new JCheckBox();
c.setSelected(false);
checkboxList.add(c);
mainPanel.add(c);
}


setUpMidi();
theFrame.setBounds(50,50,300,300);
theFrame.pack();
theFrame.setVisible(true);
}

public void setUpMidi(){

try{
sequencer = MidiSystem.getSequencer();
sequencer.open();
sequence = new Sequence(Sequence.PPQ,4);
track = sequence.createTrack();
sequencer.setTempoInBPM(120);

}

catch(Exception e){e.printStackTrace();}

}


public void buildTrackAndStart(){

int [] trackList = null;
sequence.deleteTrack(track);
track = sequence.createTrack();

for(int i = 0; i < 16; i++){

trackList = new int[16];
int key = instruments[i];

for (int j=0;j<16;j++){

JCheckBox jc = (JCheckBox) checkboxList.get(j +(16*i));
if(jc.isSelected()){
trackList[j]=key;
}
else{
trackList[j]=0;
}
}

makeTracks(trackList);
track.add(makeEvent(176,1,127,0,16));
}

track.add(makeEvent(192,9,1,0,15));
try{
sequencer.setSequence(sequence);
sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
sequencer.start();
sequencer.setTempoInBPM(120);
}
catch(Exception e){e.printStackTrace();}

}

public class MyStartListener implements ActionListener{

public void actionPerformed(ActionEvent a){

buildTrackAndStart();
}


}

public class MyStopListener implements ActionListener {

public void actionPerformed(ActionEvent a){
sequencer.stop();
}
}
public class MyUpTempoListener implements ActionListener{
public void actionPerformed(ActionEvent a){

float tempoFactor = sequencer.getTempoFactor();
sequencer.setTempoFactor((float)(tempoFactor *1.03));
}

}


public  class MyDownTempoListener implements ActionListener{
public void actionPerformed(ActionEvent a){

float tempoFactor = sequencer.getTempoFactor();
sequencer.setTempoFactor((float)(tempoFactor *.97));
}

}

public void makeTracks(int[] list){

for (int i =0; i<16; i++){
int key = list[i];

if(key != 0) {
track.add(makeEvent(144,9,key,100,i));
track.add(makeEvent(128,9,key,100,i+1));

}

}
}


public MidiEvent makeEvent(int comd,int chan, int one , int two,int tick){

MidiEvent event = null;

try{
ShortMessage a = new ShortMessage();
a.setMessage(comd,chan,one,two);
event = new MidiEvent(a,tick);
}

catch(Exception s){s.printStackTrace();}

return event;
}
public class MySendListener implements ActionListener{
public void actionPerformed(ActionEvent e){

boolean[] checkboxState = new boolean[256];
for (int i = 0; i < 256; i++){
JCheckBox check = (JCheckBox) checkboxList.get(i);

if(check.isSelected()){
checkboxState[i]= true;
}
}

try{
FileOutputStream fileStream = new FileOutputStream(new File("checkbox.ser"));
ObjectOutputStream os = new ObjectOutputStream(fileStream);
os.writeObject(checkboxState);
}catch(Exception ex){ex.printStackTrace();}
}
}

public class MyReadListener implements ActionListener{
public void actionPerformed(ActionEvent e){

boolean[] checkboxState = null;
try{
FileInputStream fileIn = new FileInputStream(new File("checkbox.ser"));
ObjectInputStream is = new ObjectInputStream(fileIn);
checkboxState = (boolean[]) is.readObject();
}
catch(Exception x){
x.printStackTrace();
}

for(int i =0; i<256; i++){
JCheckBox check = (JCheckBox) checkboxList.get(i);

if(checkboxState[i]){
check.setSelected(true);
}else{
check.setSelected(false);
}
}

sequencer.stop();
buildTrackAndStart();
}
}


}


