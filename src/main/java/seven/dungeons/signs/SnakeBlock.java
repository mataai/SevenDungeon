package seven.dungeons.signs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;

import java.util.ArrayList;
import java.util.HashMap;

public class SnakeBlock extends CaptorSign {

    private float speed;
    private int blockQuantity;
    private String direction;
    private static HashMap<String, Vector> directions = new HashMap<String, Vector>();
    private SnakeNode tail;
    private SnakeNode head;
    private float cooldown;

    public SnakeBlock(Sign sign, Game game) {
        super(sign, game);
        if(SnakeBlock.directions.isEmpty()){
            SnakeBlock.directions.put("e", new Vector(1,0,0));
            SnakeBlock.directions.put("s", new Vector(0,0,1));
            SnakeBlock.directions.put("w", new Vector(-1,0,0));
            SnakeBlock.directions.put("n", new Vector(0,0,-1));
            SnakeBlock.directions.put("u", new Vector(0,1,0));
            SnakeBlock.directions.put("d", new Vector(0,-1,0));
        }
        try {
            String[] line3 = this.sign.getLine(2).trim().split(" ");
            this.speed = Float.parseFloat(line3[0]);
            this.blockQuantity = Integer.parseInt(line3[1]);
            this.direction = line3[2];
            Location l = this.location.clone().add(0,-1,0);
            this.head = new SnakeNode(l, l.getBlock().getType(), null);
            Location nextByDirection = l.clone().add(SnakeBlock.directions.get(this.direction));
            if(nextByDirection.getBlock().getType().toString().matches("(.*CONCRETE)|(.*WOOL)") ){
                SnakeNode lastNode = new SnakeNode(nextByDirection, nextByDirection.getBlock().getType(), this.head);
                this.head.nextNode = lastNode;
                int i = 0;
                do{
                    Location nextLoc = lastNode.blockLocation;
                    if(nextLoc.clone().add(1,0,0).getBlock().getType().toString().matches("(.*CONCRETE)|(.*WOOL)") && !nextLoc.clone().add(1,0,0).equals(lastNode.previousNode.blockLocation)){
                        Location loc = nextLoc.clone().add(1,0,0);
                        lastNode.nextNode = new SnakeNode(loc, loc.getBlock().getType(), lastNode);
                        lastNode = lastNode.nextNode;
                    }
                    else if(nextLoc.clone().add(-1,0,0).getBlock().getType().toString().matches("(.*CONCRETE)|(.*WOOL)") && !(nextLoc.clone().add(-1,0,0)).equals(lastNode.previousNode.blockLocation)){
                        Location loc = nextLoc.clone().add(-1,0,0);
                        lastNode.nextNode = new SnakeNode(loc, loc.getBlock().getType(), lastNode);
                        lastNode = lastNode.nextNode;
                    }
                    else if(nextLoc.clone().add(0,0,1).getBlock().getType().toString().matches("(.*CONCRETE)|(.*WOOL)") && !nextLoc.clone().add(0,0,1).equals(lastNode.previousNode.blockLocation)){
                        Location loc = nextLoc.clone().add(0,0,1);
                        lastNode.nextNode = new SnakeNode(loc, loc.getBlock().getType(), lastNode);
                        lastNode = lastNode.nextNode;
                    }
                    else if(nextLoc.clone().add(0,0,-1).getBlock().getType().toString().matches("(.*CONCRETE)|(.*WOOL)") && !nextLoc.clone().add(0,0,-1).equals(lastNode.previousNode.blockLocation)){
                        Location loc = nextLoc.clone().add(0,0,-1);
                        lastNode.nextNode = new SnakeNode(loc, loc.getBlock().getType(), lastNode);
                        lastNode = lastNode.nextNode;
                    }
                    else if(nextLoc.clone().add(0,-1,0).getBlock().getType().toString().matches("(.*CONCRETE)|(.*WOOL)") && !nextLoc.clone().add(0,-1,0).equals(lastNode.previousNode.blockLocation)){
                        Location loc = nextLoc.clone().add(0,-1,0);
                        lastNode.nextNode = new SnakeNode(loc, loc.getBlock().getType(), lastNode);
                        lastNode = lastNode.nextNode;
                    }
                    else if(nextLoc.clone().add(0,1,0).getBlock().getType().toString().matches("(.*CONCRETE)|(.*WOOL)") && !nextLoc.clone().add(0,1,0).equals(lastNode.previousNode.blockLocation)){
                        Location loc = nextLoc.clone().add(0,1,0);
                        lastNode.nextNode = new SnakeNode(loc, loc.getBlock().getType(), lastNode);
                        lastNode = lastNode.nextNode;
                    }
                    else{
                        lastNode = null;
                    }
                    i++;
                    if(i > 200){
                        break;
                    }
                }while(lastNode != null && !lastNode.blockLocation.equals(this.head.blockLocation));
                if(lastNode == null){
                    throw new Exception("SnakeBlock is not a loop.");
                }
                this.tail = lastNode.previousNode;
                this.tail.nextNode = this.head;
                this.head.previousNode = this.tail;
                SnakeNode currentNode = this.head;
                do{
                    currentNode.blockLocation.getBlock().setType(Material.AIR);
                    currentNode = currentNode.nextNode;
                }while(currentNode != this.head);
            }

        }catch(Exception e) {
            SevenDungeons.log("SnakeBlock's third line is wrong.");
        }
    }
    
    @Override
    public void on() {
        this.game.getSigns5ticks().add(this);
        this.cooldown = 0;
    }
    
    @Override
    public void off() {
        this.game.getSigns5ticks().remove(this);
    }
    
    @Override
    public void isTriggered() {
        this.cooldown += 0.25f;
        if(this.cooldown >= this.speed){
            this.cooldown = 0;
            this.head = this.head.nextNode;
            this.head.blockLocation.getBlock().setType(this.head.blockMaterial);
            if(this.getSnakeLength() > this.blockQuantity){
                this.tail.blockLocation.getBlock().setType(Material.AIR);
                this.tail = this.tail.nextNode;
            }
        }
    }

    private int getSnakeLength(){
        int i = 0;
        SnakeNode node = this.head;
        while(node.blockLocation.getBlock().getType() != Material.AIR){
            i++;
            node = node.previousNode;
            if(i > 1000){
                break;
            }
        }
        return i;
    }

}

class SnakeNode{

    public Location blockLocation;
    public Material blockMaterial;
    public SnakeNode previousNode;
    public SnakeNode nextNode;

    public SnakeNode(Location location, Material material, SnakeNode previous){
        this.blockLocation = location;
        this.blockMaterial = material;
        this.previousNode = previous;
        this.nextNode = null;
    }

}
