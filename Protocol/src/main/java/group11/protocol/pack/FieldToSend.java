package group11.protocol.pack;


import group11.protocol.model.Cell;
import group11.protocol.model.Field;
import group11.protocol.model.Ship;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
/**
 * 
 * Class for description FieldToSend object, contains Field and User(receiver)
 */
public class FieldToSend extends Pack{
	private Field playerField;
    private String nick;
   
    /**
     * 
     * empty constructor, used only for FactoryMethod
     */ 
    public FieldToSend(){};
    
    /**
     * Default constructor for "User"
     * @param nick - Username string
     * @param field - Userfield
     */
    public FieldToSend(String nick, Field playerField){
    	this.nick= nick;
    	this.playerField= playerField;
    }
    public void setFiled(Field playerField){
    	this.playerField= playerField;
    }
    public Field getField(){
    	return playerField;
    }
    public void setNick(String nick){
    	this.nick= nick;
    }
    public String getNick(){
    	return nick;
    }
    
    public String getLogMessage(){
    	return "Send FieldToSend to - "+getNick();
    }
    
    /**
     * method for pack FieldToSend into incoming Document
     * @param doc - incoming document
     * @return Document with FieldToSend packed in
     */
    public Document writeXml(Document doc){
		Element root = doc.createElement("FieldToSend"); // корневой элемент-идентификатор документа
		 doc.appendChild(root);
		 writeDataXml(doc, root,"nick", getNick());
		 writeFieldXml(doc,root,getField(),"Field"); 
		 return doc;
	}
    
    /**
     * method for pack Field into incoming Document
     * @param doc - incoming document
     * @param root - root element of incoming document to write Field
     * @param field - Field to write
     * @param name - tag name for xml
     * @return Document with Field packed in
     */
	public Document writeFieldXml(Document doc, Element root, Field field, String name){
		 Element fPFM = doc.createElement(name);
		 root.appendChild(fPFM);
		 
		 writeDataXml(doc, fPFM,"width",Integer.toString(field.getWidth()));//размер полей по вертикали
		 writeDataXml(doc, fPFM,"height",Integer.toString(field.getHeight()));//размер полей по горизонтали
		 writeDataXml(doc, fPFM,"maxShip",Integer.toString(field.getMaxShip()));//макс. кол-во кораблей
		 writeDataXml(doc, fPFM,"numLiveShips",Integer.toString(field.getNumLiveShips()));
		 writeCellsXml(doc, fPFM, field,"Cells"); //ячейки Cells[][]
		 writeShipsXml(doc,fPFM,field.getShips(),"Ships");//Ships (сложная структура)
		 return doc;
	}
	
	 /**
     * method for pack Cells[][] into incoming Document
     * @param doc - incoming document
     * @param root - root element of incoming document to write Cells[][]
     * @param field - Cells[][] to write
     * @param name - tag name for xml
     * @return Document with Cells[][] packed in
     */
	public Document writeCellsXml(Document doc, Element root, Field field, String name){//пакуем Cels[][]
		 Element fcell = doc.createElement(name);// корневой элемент- передаваемое имя 
		 root.appendChild(fcell);
		 Element eTemp = null;
		for (int x=0; x<field.getHeight(); x++ ){
			for (int y=0; y<field.getWidth(); y++ ){
				eTemp= writeDataXml(doc, fcell,"C"+x+"C"+y,Integer.toString(field.getCell(x, y).getState()));
				eTemp.setAttribute("mark", Boolean.toString(field.getCell(x, y).isMark()));
			} 
		} 
		return doc;
	}
	
	/**
     * method for pack ArrayList<Ship> into incoming Document
     * @param doc - incoming document
     * @param root - root element of incoming document to write ArrayList<Ship>
     * @param field - ArrayList<Ship> to write
     * @param name - tag name for xml
     * @return Document with ArrayList<Ship> packed in
     */
	public Document writeShipsXml(Document doc, Element root, ArrayList<Ship> ships,String name){//пакуем ArrayList<Ship>
		Element fshp = doc.createElement(name);// - ships 
		root.appendChild(fshp);
		 
		int i = 0;
		Element eTemp = null;
		 for (Ship ship: ships){
			 eTemp =writeDataXml(doc, fshp,"ShipNumber",Integer.toString(i));		 
			 eTemp.setAttribute("shipSize", Integer.toString(ship.getSize()));
			 eTemp.setAttribute("shipHealth", Integer.toString(ship.getHealth()));
			 eTemp.setAttribute("shipState", Integer.toString(ship.getState()));
			 writeListXml(doc,fshp,ship.getListCells(),"ShipCells"); //!!!!!!!!!!!!
			 writeListXml(doc,fshp,ship.getListBorders(),"ShipBorders");
			 i++;
		 }
		 writeDataXml(doc, fshp,"shipCount",Integer.toString(i));
		 return doc;
	}
	
	/**
     * method for pack List<Cell> into incoming Document
     * (can be used for pack listCells or listBorders)
     * @param doc - incoming document
     * @param root - root element of incoming document to write List<Cell>
     * @param field - List<Cell> to write
     * @param name - tag name for xml
     * @return Document with List<Cell> packed in
     */
	public Document writeListXml(Document doc, Element root, List<Cell> cells,String name){
		Element fcell = doc.createElement(name); 
		root.appendChild(fcell);
		Element eTemp = null;
		
		 for (Cell cell: cells){
			 eTemp= writeDataXml(doc, fcell,"L"+cell.x+"L"+cell.y,Integer.toString(cell.getState()));
			 eTemp.setAttribute("mark", Boolean.toString(cell.isMark()));
		 }
		 return doc;
	}
	
  //****************************************receive part****************************************/
	
	/**
	 * 
     * method for unpack FieldToSend from incoming Document
     * @param doc - incoming document
     * @return FieldToSend unpacked from incoming document
     */
	public FieldToSend readXml(Document doc){
		return (new FieldToSend(GetData(doc,"nick"),GetField(doc)));
	}
	
	/**
	 * 
     * method for unpack Field from incoming Document
     * @param doc - incoming document
     * @return Field unpacked from incoming document
     */
	public Field GetField(Document doc){
		Field field = new Field(Integer.parseInt(GetData(doc,"width")),Integer.parseInt(GetData(doc,"height")),
							Integer.parseInt(GetData(doc,"maxShip"))); 
		field.setNumLiveShips(Integer.parseInt(GetData(doc,"numLiveShips")));
		field.setCells(GetCells(doc,field.getWidth(),field.getHeight()));
		field.setShips(getShips(doc));
		
		return field;
	}
	
	/**
	 * 
     * method for unpack ArrayList<Ship> from incoming Document
     * @param doc - incoming document
     * @return ArrayList<Ship> unpacked from incoming document
     */
	public ArrayList<Ship> getShips(Document doc){
		ArrayList<Ship> ships = new ArrayList<Ship>();
		Element eTemp = null;
		NodeList nodeShips = doc.getElementsByTagName("ShipNumber");
		for (int s = 0; s < nodeShips.getLength(); s++) {
			eTemp = (Element)nodeShips.item(s);
			//System.out.println("===ShipNumber= "+s);
			ships.add(s,new Ship(Integer.parseInt(eTemp.getAttributes().getNamedItem("shipSize").getNodeValue())));
			ships.get(s).setHealth(Integer.parseInt(eTemp.getAttributes().getNamedItem("shipHealth").getNodeValue()));
			ships.get(s).setState(Integer.parseInt(eTemp.getAttributes().getNamedItem("shipState").getNodeValue()));
			
			ships.get(s).setListCells(getListCells(doc,"ShipCells",s));
			ships.get(s).setListBorders(getListCells(doc,"ShipBorders",s));
		}	
		return ships;
	}
	
	/**
	 * 
     * method for unpack List<Cell> from incoming Document
     * (can be used for unpack ShipCells/ShipBorders)
     * @param doc - incoming document
     * @return List<Cell> unpacked from incoming document
     */
	public List <Cell> getListCells(Document doc,String teg,int num){ 
		List<Cell> list = new ArrayList<Cell>();
		NodeList nodeList = doc.getElementsByTagName(teg); 
		Cell cell = null; 
			Element eTemp = (Element)nodeList.item(num);
			for (int x = 0;x < Integer.parseInt(GetData(doc,"width")); x++){
				for (int y = 0;y < Integer.parseInt(GetData(doc,"height")); y++){
			
					if (eTemp.getElementsByTagName("L"+x+"L"+y).getLength()>0){
						cell = new Cell(x,y);
						cell.setState(Integer.parseInt(eTemp.getElementsByTagName("L"+x+"L"+y).item(0).getFirstChild().getNodeValue()));
						cell.setMark(Boolean.valueOf  (eTemp.getElementsByTagName("L"+x+"L"+y).item(0).getAttributes().item(0).getNodeValue()));
						list.add(cell);
					}
				}
			}
		return list;
	}
	
	/**
	 * 
     * method for unpack Cell[][] from incoming Document
     * @param doc - incoming document
     * @return Cell[][] unpacked from incoming document
     */
	public Cell[][] GetCells(Document doc,int width, int height){
		Cell[][] cells = new Cell[width][height];
		for (int x = 0;x < width; x++){
			for (int y = 0;y < width; y++){
				cells[x][y] = new Cell(x, y); 
				cells[x][y].setMark(Boolean.valueOf(doc.getElementsByTagName("C"+x+"C"+y).item(0).getAttributes().item(0).getNodeValue()));
				cells[x][y].setState(Integer.parseInt(doc.getElementsByTagName("C"+x+"C"+y).item(0).getFirstChild().getNodeValue()));
			}
		}
		return cells;
	}
	
}
