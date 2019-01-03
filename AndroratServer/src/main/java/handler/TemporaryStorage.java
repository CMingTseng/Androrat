package handler;

import java.io.IOException;
import java.util.ArrayList;

import Packet.TransportPacket;

import inout.Protocol;


/*
 * Class servant d'espace de stockage temporaire 
 * aux donnes reues
 * 
 * 
 * Mthode addByteData():
 * permet de complter les donnes en cours de rception
 * en les rajoutant dans un tableaux d'octets
 * 
 * Mthode setLastPacketInfo():
 * permet d'inscrire les informations sur le dernier paquet reu
 */
public class TemporaryStorage
{
	//les donnes
	private ArrayList<byte[]> data_temp;

	private byte[] final_data;
	
	//les informations sur le dernier paquet reu
	private int total_length;
	private int last_packet_position;
	private int size_counter;
	private boolean end;
	
	private short data_type;
	
	
	//constructeur: en cours de reception(commande)
	public TemporaryStorage()
	{
		//initialisation de l'espace de stockage
		data_temp = new ArrayList<byte[]>();
		last_packet_position = -1;
		size_counter = 0;
		
	}

	public void reset() {
		data_temp = new ArrayList<byte[]>();
		last_packet_position = -1;
		size_counter = 0;
		end = false;
	}

	
	public short addPacket(TransportPacket packet)
	{

		
		if(packet.getNumSeq() != (last_packet_position+1))
			return Protocol.PACKET_LOST;
		
		
		//si la suite des donnes est attendue
        if(!end)
		{	
        	//System.out.println("on rajoute des donnes attendues");

        	total_length = packet.getTotalLength();
			end = packet.isLast();
			size_counter+= packet.getLocalLength();
			data_temp.add(packet.getData());
			
			//si on attend encore des donnes
			if(!end)
			{
				System.out.println("Paquet "+packet.getNumSeq());
				last_packet_position ++;
			  return Protocol.PACKET_DONE;
			}
			else//sinon (si c'est la fin)
			{
	
				if(size_counter != total_length)
					return Protocol.SIZE_ERROR;
				
				int i = 0;
				final_data = new byte[total_length];
				for(int n = 0;n<data_temp.size();n++)
					for(int p = 0;p<data_temp.get(n).length;p++,i++)
						final_data[i] = data_temp.get(n)[p];
						
				return Protocol.ALL_DONE;
			}
			
		}
		else 
			//sinon erreur
		   return Protocol.NO_MORE;
		
	}

	
	public ArrayList<byte[]> getByteData()
	{
		return data_temp;
	}
	
	public byte[] getFinalData()
	{
		return final_data;
	}
	
	public int getLastPacketPositionReceived()
	{
		return last_packet_position;
	}
	
	public int getTotalSize()
	{
		return total_length;
	}
}
