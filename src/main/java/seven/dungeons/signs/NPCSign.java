package seven.dungeons.signs;

import org.bukkit.block.Sign;
import seven.dungeons.Game;
import seven.dungeons.SevenDungeons;
import seven.dungeons.npcs.SerCreighton;
import seven.dungeons.npcs.*;

public class NPCSign extends ActivableSign {
    
    private SevenNPC sevenNPC;

    public NPCSign(Sign sign, Game game) {
        super(sign, game);
        this.location.add(0.5,0,0.5);
        try {
            switch(this.sign.getLine(2).trim()) {
                case "balloonburst" : this.sevenNPC = new BalloonBurstNPC(this); break;
                case "bob" : this.sevenNPC = new ColorPanelNPC(this); break;
                case "bonnie" : this.sevenNPC = new Bonnie(this); break;
                case "carlos" : this.sevenNPC = new Carlos(this); break;
                case "gh1" : this.sevenNPC = new SlotMachineNPC(this); break;
                case "gh2" : this.sevenNPC = new GameHouse2(this); break;
                case "fishfrenzy" : this.sevenNPC = new FishFrenzyNPC(this); break;
                case "gus" : this.sevenNPC = new Gus(this); break;
                case "hansel" : this.sevenNPC = new Hansel(this); break;
                case "helmer" : this.sevenNPC = new Helmer(this); break;
                case "joe" : this.sevenNPC = new MechanicJoe(this); break;
                case "judy" : this.sevenNPC = new Judy(this); break;
                case "larry" : this.sevenNPC = new Larry(this); break;
                case "lavatower" : this.sevenNPC = new LavaTowerNPC(this); break;
                case "lenny" : this.sevenNPC = new Lenny(this); break;
                case "lindy" : this.sevenNPC = new Lindy(this); break;
                case "minecart" : this.sevenNPC = new MinecartMadnessNPC(this); break;
                case "mushroom" : this.sevenNPC = new MushroomMayhemNPC(this); break;
                case "nald" : this.sevenNPC = new Nald(this); break;
                case "pietro" : this.sevenNPC = new Pietro(this); break;
                case "pietro2" : this.sevenNPC = new Pietro2(this); break;
                case "sabrina" : this.sevenNPC = new Sabrina(this); break;
                case "sandy": this.sevenNPC = new Sandy(this); break;
                case "sercreighton" : this.sevenNPC = new SerCreighton(this); break;
                case "shawn" : this.sevenNPC = new Shawn(this); break;
                case "simon" : this.sevenNPC = new Simon(this); break;
                case "spaceballoons" : this.sevenNPC = new SpaceBalloons(this); break;
                case "spacefishing" : this.sevenNPC = new SpaceFishing(this); break;
                case "spacemine" : this.sevenNPC = new SpaceMinecart(this); break;
                case "spacepig" : this.sevenNPC = new SpacePigRun(this); break;
                case "stardust" : this.sevenNPC = new StardustNPC(this); break;
                case "ronda" : this.sevenNPC = new LaserGoRoundNPC(this); break;
                case "vanna" : this.sevenNPC = new Vanna(this); break;
            }
        }catch(Exception e) {
            SevenDungeons.log("NPC's third line is wrong or empty at ." + this.location.toString());
        }
    }
    
    @Override
    public void on() {
        this.sevenNPC.getNPC().spawn(this.location);
        this.game.getNPCs().add(this);
    }
    
    @Override
    public void off() {
        this.game.getNPCs().remove(this);
        this.sevenNPC.getNPC().despawn();
        this.sevenNPC.getNPC().destroy();
    }
    
    public SevenNPC getSevenNPC() {
        return this.sevenNPC;
    }
    
    public Game getGame() {
        return this.game;
    }
    
    public void signalNPC(int id) {
        this.sevenNPC.signal(id);
    }
    
    public Sign getSign() {
    	return this.sign;
    }

}
