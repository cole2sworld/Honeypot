/**
 * 
 */
package com.argo.bukkit.honeypot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/** Code courtesy of @krinsdeath
 * 
 * @author morganm
 *
 */
public class CuboidRegion {

    private World world;
    private Vector minimum;
    private Vector maximum;

    public CuboidRegion(com.sk89q.worldedit.regions.Region region, World world) {
    	this(region.getMinimumPoint(), region.getMaximumPoint(), world);
    }
    public CuboidRegion(com.sk89q.worldedit.Vector pos1, com.sk89q.worldedit.Vector pos2, World world) {
        this.world = world;
        Vector left = new Vector(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ());
        Vector right = new Vector(pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ());
        this.minimum = Vector.getMinimum(left, right);
        this.maximum = Vector.getMaximum(left, right);
    }

    public CuboidRegion(Vector left, Vector right, World world) {
        this.world = world;
        this.minimum = Vector.getMinimum(left, right);
        this.maximum = Vector.getMaximum(left, right);
    }

    public Vector getMinimumPoint() {
        return minimum;
    }

    public Vector getMaximumPoint() {
        return maximum;
    }
    
    public World getWorld() { return world; }

    public boolean contains(Location loc) {
        if (!loc.getWorld().equals(this.world)) { return false; }
        if (loc.getBlockX() >= minimum.getBlockX() && loc.getBlockX() <= maximum.getBlockX()) {
            if (loc.getBlockY() >= minimum.getBlockY() && loc.getBlockY() <= maximum.getBlockY()) {
                if (loc.getBlockZ() >= minimum.getBlockZ() && loc.getBlockZ() <= maximum.getBlockZ()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(Object o) {
    	if( o == null )
    		return false;
    	if( !(o instanceof CuboidRegion) )
    		return false;
    	
    	CuboidRegion region = (CuboidRegion) o;
    	
    	if( !region.getWorld().getName().equals(getWorld().getName()) )
    		return false;
    	if( region.minimum.getBlockX() != minimum.getBlockX() )
    		return false;
    	if( region.minimum.getBlockY() != minimum.getBlockY() )
    		return false;
    	if( region.minimum.getBlockZ() != minimum.getBlockZ() )
    		return false;
    	if( region.maximum.getBlockX() != maximum.getBlockX() )
    		return false;
    	if( region.maximum.getBlockY() != maximum.getBlockY() )
    		return false;
    	if( region.maximum.getBlockZ() != maximum.getBlockZ() )
    		return false;
    	
    	return true;
    }
    
    @Override
    public int hashCode() {
    	return world.getName().hashCode() + minimum.getBlockX() + minimum.getBlockY()
    		+ minimum.getBlockZ() + maximum.getBlockX() + maximum.getBlockY()
    		+ maximum.getBlockZ();
    }
    
    public String exportAsString() {
    	return world.getName()
    		+","+minimum.getBlockX()
    		+","+minimum.getBlockY()
    		+","+minimum.getBlockZ()
    		+","+maximum.getBlockX()
    		+","+maximum.getBlockY()
    		+","+maximum.getBlockZ();
    }
    
    public static CuboidRegion importFromString(String str) {
    	String[] elements = str.split(",");
    	int i = 0;
    	String worldName = elements[i++];
    	Integer minX = Integer.parseInt(elements[i++]);
    	Integer minY = Integer.parseInt(elements[i++]);
    	Integer minZ = Integer.parseInt(elements[i++]);
    	Integer maxX = Integer.parseInt(elements[i++]);
    	Integer maxY = Integer.parseInt(elements[i++]);
    	Integer maxZ = Integer.parseInt(elements[i++]);
    	
    	World world = Bukkit.getServer().getWorld(worldName);
    	com.sk89q.worldedit.Vector minV = new com.sk89q.worldedit.Vector(minX, minY, minZ);
    	com.sk89q.worldedit.Vector maxV = new com.sk89q.worldedit.Vector(maxX, maxY, maxZ);
    	
    	return new CuboidRegion(minV, maxV, world);
    }
}
