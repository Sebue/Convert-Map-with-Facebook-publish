package model;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class MapConverter {
	private final int TOLERANCE = 200;
	private final Random random;
	private BufferedImage map;
	List<List<Point>> fields = new ArrayList<>();
	Map<Point, Set<Integer>> borders = new HashMap<>();
	List<Set<Integer>> relation = new ArrayList<>();
	List<Integer> relationSize = new ArrayList<>();
	
	public MapConverter(BufferedImage map){
		this.map = map;
		random = new Random();
	}

	public BufferedImage getConvertedMap(){
		sharpMap();
		while(findWhiteField()){
		}
		System.out.println("Countries: " + fields.size());
		makeRelation();
		colorMap();
		return map;
	}
	
	private void makeRelation(){
		for(int i = 0; i < fields.size(); i++){
			Set<Integer> temp = new HashSet<Integer>();
			borders.values().stream()
				.filter(neighbour -> neighbour.contains(relation.size()))
				.forEach(neighbour -> temp.addAll(neighbour));
			temp.remove(relation.size());
			relation.add(temp);
			if(!relationSize.contains(temp.size())){
				relationSize.add(temp.size());
			}
		}
	}

	private void colorMap() {
		Collections.sort(relationSize);
		Collections.reverse(relationSize);
		
		for(Integer size : relationSize){
			relation.stream()
				.filter(rel -> rel.size() == size)
				.forEach(rel -> colorField(relation.indexOf(rel), pickColorForField(rel)));
		}
	}
	
	private Color pickColorForField(Set<Integer> relation){
		List<Color> neighboursColor = new ArrayList<Color>();
		for(Integer index : relation){
			Point firstPoint = fields.get(index).get(0);
			neighboursColor.add(new Color(map.getRGB(firstPoint.x, firstPoint.y)));
		}
		
		if(!neighboursColor.contains(Color.BLUE)){
			return Color.BLUE;
		}
		else if(!neighboursColor.contains(Color.GREEN)){
			return Color.GREEN;
		}
		else if(!neighboursColor.contains(Color.YELLOW)){
			return Color.YELLOW;
		}
		else if(!neighboursColor.contains(Color.RED)){
			return Color.RED;
		}
		else{
			return Color.PINK;
		}
	}
	
	private void colorField(int fieldNumber, Color color){
		for(Point point : fields.get(fieldNumber)){
			map.setRGB(point.x, point.y, color.getRGB());
		}
	}
	
	private void sharpMap(){
		for(int x = 0; x < map.getWidth(); x++){
			for(int y = 0; y < map.getHeight(); y++)
			{
				Color pixel = new Color(map.getRGB(x, y));
				if(pixel.getBlue() > TOLERANCE && pixel.getGreen() > TOLERANCE && pixel.getRed() > TOLERANCE){
						map.setRGB(x, y, Color.WHITE.getRGB());
				}
				else{
						map.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
		}
	}
	
	private boolean findWhiteField(){
		for(int x = 0; x < map.getWidth(); x++){
			for(int y = 0; y < map.getHeight(); y++)
			{
				if(isWhite(x, y, false)){
					findNeighbourWhitePixel(new Point(x, y));
					return true;
				}
			}
		}
		return false;
	}
	
	private void findNeighbourWhitePixel(Point pixel){
		Stack<Point> stack = new Stack<>();
		stack.push(pixel);
		Set<Point> singleField = new HashSet<Point>();
		
		while(!stack.isEmpty()){
			Point point = stack.pop();
			singleField.add(point);
			map.setRGB(point.x, point.y, Color.GRAY.getRGB());
			checkAllNeigbours(point, stack);
		}
		fields.add(new ArrayList<Point>(singleField));
	}
	
	private void checkAllNeigbours(Point point, Stack<Point> stack){
		Point right = new Point(point.x+1, point.y);
		if(right.x < map.getWidth() && isWhite(right.x, right.y, true)){
			stack.push(right);
		}
		
		Point left = new Point(point.x-1, point.y);
		if(left.x >= 0 && isWhite(left.x, left.y, true)){
			stack.push(left);
		}
		
		Point down = new Point(point.x, point.y+1);
		if(down.y < map.getHeight() && isWhite(down.x, down.y, true)){
			stack.push(down);
		}
		
		Point up = new Point(point.x, point.y-1);
		if(up.y >= 0 && isWhite(up.x, up.y, true)){
			stack.push(up);
		}
	}
	
	private boolean isWhite(int x, int y, boolean checkBlack){
		Color pixel = new Color(map.getRGB(x, y));
		if(pixel.getRGB() == Color.WHITE.getRGB()){
			return true;
		}
		else if(checkBlack) {
			handleBorderPixel(new Point(x,y));
		}
		return false;
	}
	
	private void handleBorderPixel(Point point){
		if(borders.containsKey(point)){
			borders.get(point).add(fields.size());
		}
		else{
			Set<Integer> neighbours = new HashSet<Integer>();
			neighbours.add(fields.size());
			borders.put(point, neighbours);
		}
	}

}