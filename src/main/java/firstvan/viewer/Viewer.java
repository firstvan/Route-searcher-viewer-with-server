package firstvan.viewer;

import java.awt.BorderLayout;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultWaypoint;


import java.io.IOException;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JOptionPane;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;





public class Viewer
{
	final static private JXMapKit jmk = new JXMapKit();
	final static private List<GeoPosition> csm = new ArrayList<>();
	final static private Set<Waypoint> wayPoints = new HashSet<>();
	static private javax.swing.JButton startButton;
	static GeoPosition p1 = new GeoPosition(0, 0);
	static GeoPosition p2 = new GeoPosition(0, 0);	
	static JFrame frame = new JFrame("Route");
	
	public static void savePoint(int i, GeoPosition gp)
	{
		if(i == 1)
		{
			p1 = gp;
		}
		else if(i == 2)
		{
			p2 = gp;
		}
		
		frissit();
	}
	
	public static void frissit()
	{
			
		Set<Waypoint> waypoints;
		waypoints = new HashSet<>();
				
		if(p1 != new GeoPosition(0, 0)){
			waypoints.add(new DefaultWaypoint(p1));
		}
		if(p2 != new GeoPosition(0, 0)){
			waypoints.add(new DefaultWaypoint(p2));
		}
				
		WaypointPainter<Waypoint> waypointPainter  = new WaypointPainter<>();
		waypointPainter.setWaypoints(waypoints);
		
		List<Painter<JXMapViewer>> painters;
		painters = new ArrayList<>();
		painters.add(waypointPainter);
		
		CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
		jmk.getMainMap().setOverlayPainter(painter);	
	}
	
	public static void draw(){
		WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
		  waypointPainter.setWaypoints(wayPoints);
			  
		  RoutePainter routePainter = new RoutePainter(csm); 
			
		  List<Painter<JXMapViewer>> painters;
		  painters = new ArrayList<>();
		  painters.add(routePainter);
		  painters.add(waypointPainter);
			
		  CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
		  jmk.getMainMap().setOverlayPainter(painter);	 
		
	}
	
	public static void startB() {
		
	if((p1.getLatitude() == 0 && p1.getLongitude() == 0) || (p2.getLatitude() == 0 && p2.getLongitude() == 0)){
		JOptionPane.showMessageDialog(frame, "Please add start and end point");	
	}
	else{
		
		try{
			String url = "http://86.101.198.156:5000/"+p1.getLatitude()+"-"+p1.getLongitude()+"-"+p2.getLatitude()+"-"+p2.getLongitude();

			URL cord= new URL(url);
			URLConnection urlc = cord.openConnection();
			BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                urlc.getInputStream()));
			
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = in.readLine()) != null;) {
			  builder.append(line).append("\n");
			}  	

				
			JSONTokener jt = new JSONTokener(builder.toString());
			
			JSONObject jo = new JSONObject(jt);
			
			csm.clear();
			
			int seged=0;
			boolean talalt = true;
			
			while(jo.has(Integer.toString(++seged))){
				JSONArray ja = jo.getJSONArray(Integer.toString(seged));
				if(ja.getString(0).equals("Route")){
					JOptionPane.showMessageDialog(frame, "Route not found");
					talalt = false;
					break;
				}
				else{
					csm.add(new GeoPosition(ja.getDouble(0), ja.getDouble(1)));
				}
			}
			
			if(talalt){
			wayPoints.clear();
		  	wayPoints.add(new DefaultWaypoint(csm.get(0)));
		  	wayPoints.add(new DefaultWaypoint(csm.get(csm.size()-1)));
		  	draw();
			}
		} catch (JSONException e) {System.out.println(e);}
		catch(MalformedURLException e){
			System.out.println(e);
		}
		catch(IOException e) {JOptionPane.showMessageDialog(frame, "Failed to connect to server.");}
		
	}
	}
	
	
  public static void main(String[] argv){
	  
      startButton = new javax.swing.JButton();
	  startButton.setText("Indít");
	  startButton.addActionListener(new java.awt.event.ActionListener(){
		  @Override
		  public void actionPerformed(java.awt.event.ActionEvent evn){
			  startB();
		  }
	  });
	  
      
      
      
        TileFactoryInfo info = new OSMTileFactoryInfo();
	DefaultTileFactory dinfo = new DefaultTileFactory(info);
	jmk.setTileFactory(dinfo);
	 
	  
	jmk.setZoom(5);
	jmk.setAddressLocation(new GeoPosition(47.532130, 21.624180));
	jmk.setAddressLocationShown(false);
	  
	jmk.getMainMap().addMouseListener(new MouseListener(){
		  @Override
		  public void mouseClicked(MouseEvent e)
		  {

			  GeoPosition click1 = jmk.getMainMap().convertPointToGeoPosition(e.getPoint());
			  if(e.getButton() == MouseEvent.BUTTON1)
				  savePoint(1, click1);
			  else if(e.getButton() == MouseEvent.BUTTON3)
				  savePoint(2, click1);
		  }
		  
		  @Override
		  public void mouseExited(MouseEvent e)
		  {
			  //pass
		  }

		  @Override
		  public void mouseEntered(MouseEvent e)
		  {
			  //pass
		  }

		  @Override
		  public void mousePressed(MouseEvent e)
		  {
			  //pass
		  }

		  @Override
		  public void mouseReleased(MouseEvent e)
		  {
			  //pass
		  }
	});
	  


	  
	frame.setLayout(new BorderLayout());	  
	
	frame.add(startButton, BorderLayout.NORTH);
	frame.add(jmk);

	frame.setSize(1280, 720);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);	
	  
	  
  }

}