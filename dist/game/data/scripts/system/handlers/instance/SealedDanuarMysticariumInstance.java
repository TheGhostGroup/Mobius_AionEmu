/*
 * This file is part of the Aion-Emu project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package system.handlers.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author Rinzler (Encom)
 */
@InstanceID(300480000)
public class SealedDanuarMysticariumInstance extends GeneralInstanceHandler
{
	private Race spawnRace;
	private Future<?> raidTask;
	private boolean isStartTimer1 = false;
	private boolean isStartTimer2 = false;
	private Map<Integer, StaticDoor> doors;
	protected boolean isInstanceDestroyed = false;
	// Mini Game 1
	private final List<Npc> AncientBox1 = new ArrayList<>();
	private final List<Npc> AncientBox2 = new ArrayList<>();
	// Mini Game 2
	private final List<Npc> TestSubject48012C = new ArrayList<>();
	private final List<Npc> TestSubject48013C = new ArrayList<>();
	private final List<Npc> TestSubject48015C = new ArrayList<>();
	private final List<Npc> TestSubject48023B = new ArrayList<>();
	private final List<Npc> TestSubject48027B = new ArrayList<>();
	private final List<Npc> TestSubject48025B = new ArrayList<>();
	private final List<Npc> TestSubject48039A = new ArrayList<>();
	private final List<Npc> TestSubject48123A = new ArrayList<>();
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance)
	{
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
	}
	
	@Override
	public void onDropRegistered(Npc npc)
	{
		final Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		final int npcId = npc.getNpcId();
		switch (npcId)
		{
			case 702700: // Ancient Box.
			case 702702: // Ancient Box.
			{
				switch (Rnd.get(1, 3))
				{
					case 1:
					{
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000224, 1)); // Solid Prison Key.
						break;
					}
					case 2:
					{
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000225, 1)); // Shabby Prison Key.
						break;
					}
					case 3:
					{
						dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000226, 1)); // Hidden Prison Key.
						break;
					}
				}
				break;
			}
		}
	}
	
	private void SpawnSealedMysticariumRace()
	{
		final int soloSet = spawnRace == Race.ASMODIANS ? 731584 : 731583;
		final int soloGuide = spawnRace == Race.ASMODIANS ? 805220 : 805219;
		spawn(soloSet, 167.55927f, 122.991554f, 231.67175f, (byte) 118);
		spawn(soloGuide, 171.01581f, 128.56917f, 231.66145f, (byte) 107);
	}
	
	@Override
	public void onEnterInstance(Player player)
	{
		super.onInstanceCreate(instance);
		if (spawnRace == null)
		{
			spawnRace = player.getRace();
			SpawnSealedMysticariumRace();
		}
	}
	
	@Override
	public void onDie(Npc npc)
	{
		switch (npc.getObjectTemplate().getTemplateId())
		{
			case 219980: // Sheban Espionage Cavalry.
			case 219981: // Sheban Espionage Combatant.
			case 219982: // Sheban Espionage Assassin.
			case 219983: // Sheban Grunt.
			case 219984: // Sheban Gunner.
			case 219985: // MuMu Test Subject.
			case 219986: // Sprigg Test Subject.
			{
				despawnNpc(npc);
				break;
			}
			case 219987: // Doomtread Kurores.
			{
				despawnNpc(npc);
				deleteNpc(219979); // Ancient Danuar Relic.
				raidTask.cancel(true);
				sendMsg("[Congratulation]: you finish <Sealed Danuar Mysticarium 4.8 KR version>");
				spawn(701572, 556.5924f, 416.37885f, 96.81002f, (byte) 43); // Sealed Danuar Mysticarium Exit.
				break;
			}
		}
	}
	
	private void startAncientBoxTimer()
	{
		// Locate the prison keys to defeat the monsters inside.
		sendMsgByRace(1402801, Race.PC_ALL, 0);
		// All monsters and key boxes in the library have disappeared.
		sendMsgByRace(1402805, Race.PC_ALL, 300000);
		// All monsters and key boxes in the library will disappear in 1 minute.
		sendMessage(1402802, 4 * 60 * 1000);
		ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			spawn(702715, 172.09422f, 206.95068f, 188.01584f, (byte) 118); // Experimental Prison Teleporter.
			AncientBox1.get(0).getController().onDelete();
			AncientBox1.get(1).getController().onDelete();
			AncientBox1.get(2).getController().onDelete();
			AncientBox1.get(3).getController().onDelete();
			AncientBox1.get(4).getController().onDelete();
			AncientBox1.get(5).getController().onDelete();
			AncientBox1.get(6).getController().onDelete();
			AncientBox1.get(7).getController().onDelete();
			AncientBox1.get(8).getController().onDelete();
			AncientBox1.get(9).getController().onDelete();
			AncientBox1.get(10).getController().onDelete();
			AncientBox1.get(11).getController().onDelete();
			AncientBox1.get(12).getController().onDelete();
			AncientBox1.get(13).getController().onDelete();
			AncientBox1.get(14).getController().onDelete();
			AncientBox1.get(15).getController().onDelete();
			AncientBox1.get(16).getController().onDelete();
			AncientBox1.get(17).getController().onDelete();
			AncientBox1.get(18).getController().onDelete();
			AncientBox1.get(19).getController().onDelete();
			// Ancient Box 2
			AncientBox2.get(0).getController().onDelete();
			AncientBox2.get(1).getController().onDelete();
			AncientBox2.get(2).getController().onDelete();
			AncientBox2.get(3).getController().onDelete();
			AncientBox2.get(4).getController().onDelete();
			AncientBox2.get(5).getController().onDelete();
			AncientBox2.get(6).getController().onDelete();
		}, 300000); // 5 Minute.
	}
	
	private void startTestSubjectPrisonTimer()
	{
		// Catch the criminals hiding in the Abyss Rift.
		sendMsgByRace(1402811, Race.PC_ALL, 0);
		// All monsters will disappear in 30 seconds.
		sendMsgByRace(1402814, Race.PC_ALL, 270000);
		// All monsters will disappear in a moment.
		sendMsgByRace(1402815, Race.PC_ALL, 290000);
		// All monsters have disappeared and the door to the Artifact Repository has opened.
		sendMsgByRace(1402816, Race.PC_ALL, 300000);
		// All monsters will disappear in 2 minutes.
		sendMessage(1402812, 3 * 60 * 1000);
		// All monsters will disappear in 1 minute.
		sendMessage(1402813, 4 * 60 * 1000);
		ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			TestSubject48012C.get(0).getController().onDelete();
			TestSubject48013C.get(0).getController().onDelete();
			TestSubject48015C.get(0).getController().onDelete();
			TestSubject48023B.get(0).getController().onDelete();
			TestSubject48025B.get(0).getController().onDelete();
			TestSubject48027B.get(0).getController().onDelete();
			TestSubject48039A.get(0).getController().onDelete();
			TestSubject48123A.get(0).getController().onDelete();
			spawn(702717, 331.80203f, 495.63107f, 147.172f, (byte) 44); // Sealed Danuar Mysticarium Exit.
		}, 300000); // 5 Minute.
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc)
	{
		switch (npc.getNpcId())
		{
			case 219979: // Ancient Danuar Relic.
			{
				/**
				 * Start Mini Game 3
				 */
				// Protect the ancient Danuar artifacts from Beritra's troops until reinforcements arrive.
				sendMsgByRace(1402821, Race.PC_ALL, 0);
				// Monsters will attack in a moment.
				sendMsgByRace(1402830, Race.PC_ALL, 3000);
				ThreadPoolManager.getInstance().schedule((Runnable) () -> doors.get(4).setOpen(true), 5000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> startMysticariumRaid1(), 10000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> startMysticariumRaid2(), 30000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> startMysticariumRaid3(), 50000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> startMysticariumRaid4(), 70000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () ->
				{
					startMysticariumRaid5();
					// An attack by Beritra's troops is imminent.
					sendMsgByRace(1402822, Race.PC_ALL, 0);
					// Additional Beritra troops have joined the attack.
					sendMsgByRace(1402824, Race.PC_ALL, 3000);
				}, 90000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> startMysticariumRaid6(), 110000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> startMysticariumRaid7(), 130000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () ->
				{
					startMysticariumRaid8();
					// An attack by Beritra's troops is imminent.
					sendMsgByRace(1402822, Race.PC_ALL, 0);
					// Additional Beritra troops have joined the attack.
					sendMsgByRace(1402824, Race.PC_ALL, 3000);
				}, 150000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> startMysticariumRaid9(), 170000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> startMysticariumRaid10(), 190000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () ->
				{
					startMysticariumRaid11();
					// An attack by Beritra's troops is imminent.
					sendMsgByRace(1402822, Race.PC_ALL, 0);
					// Additional Beritra troops have joined the attack.
					sendMsgByRace(1402824, Race.PC_ALL, 3000);
				}, 210000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () ->
				{
					startMysticariumRaid12();
					// An attack by Beritra's troops is imminent.
					sendMsgByRace(1402822, Race.PC_ALL, 0);
					// Additional Beritra troops have joined the attack.
					sendMsgByRace(1402824, Race.PC_ALL, 3000);
				}, 230000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> startMysticariumRaid13(), 250000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () ->
				{
					startMysticariumRaid14();
					// An attack by Beritra's troops is imminent.
					sendMsgByRace(1402822, Race.PC_ALL, 0);
					// Additional Beritra troops have joined the attack.
					sendMsgByRace(1402824, Race.PC_ALL, 3000);
				}, 270000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> startMysticariumRaid15(), 290000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () ->
				{
					startMysticariumRaid16();
					// An attack by Beritra's troops is imminent.
					sendMsgByRace(1402822, Race.PC_ALL, 0);
					// Additional Beritra troops have joined the attack.
					sendMsgByRace(1402824, Race.PC_ALL, 3000);
				}, 310000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () ->
				{
					startMysticariumRaid17();
					// An attack by Beritra's troops is imminent.
					sendMsgByRace(1402822, Race.PC_ALL, 0);
					// Additional Beritra troops have joined the attack.
					sendMsgByRace(1402824, Race.PC_ALL, 3000);
				}, 330000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () -> startMysticariumRaid18(), 350000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () ->
				{
					startMysticariumRaid19();
					// An attack by Beritra's troops is imminent.
					sendMsgByRace(1402822, Race.PC_ALL, 0);
					// Additional Beritra troops have joined the attack.
					sendMsgByRace(1402824, Race.PC_ALL, 3000);
				}, 370000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () ->
				{
					startMysticariumRaid20();
					// Thanks to our reinforcements, all Beritra's troops have been annihilated.
					// It appears their Commander is preparing for a final offensive.
					sendMsgByRace(1402827, Race.PC_ALL, 0);
				}, 390000);
				raidTask = ThreadPoolManager.getInstance().schedule((Runnable) () ->
				{
					startMysticariumBoss();
					// The Commander of the Beritra troops has destroyed this place. Eliminate the enemy Commander!
					sendMsgByRace(1402828, Race.PC_ALL, 0);
				}, 410000);
				break;
			}
		}
	}
	
	@Override
	public void onEnterZone(Player player, ZoneInstance zone)
	{
		/**
		 * Start Mini Game 1
		 */
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("DANUAR_MYSTICARIUM_300480000"))
		{
			if (!isStartTimer1)
			{
				isStartTimer1 = true;
				System.currentTimeMillis();
				instance.doOnAllPlayers(player1 ->
				{
					if (player1.isOnline())
					{
						startAncientBoxTimer();
						PacketSendUtility.sendPacket(player1, new SM_QUEST_ACTION(0, 300));
					}
				});
			}
			// Ancient Box 1
			AncientBox1.add((Npc) spawn(702700, 199.0f, 184.0f, 187.97449f, (byte) 77));
			AncientBox1.add((Npc) spawn(702700, 154.0f, 186.0f, 187.8586f, (byte) 76));
			AncientBox1.add((Npc) spawn(702700, 156.35754f, 235.06516f, 233.98196f, (byte) 3));
			AncientBox1.add((Npc) spawn(702700, 187.53642f, 206.76617f, 239.05104f, (byte) 37));
			AncientBox1.add((Npc) spawn(702700, 154.0f, 190.0f, 239.38611f, (byte) 37));
			AncientBox1.add((Npc) spawn(702700, 119.0f, 206.0f, 214.17435f, (byte) 107));
			AncientBox1.add((Npc) spawn(702700, 209.41882f, 243.28488f, 187.8586f, (byte) 89));
			AncientBox1.add((Npc) spawn(702700, 216.0f, 238.0f, 239.35832f, (byte) 89));
			AncientBox1.add((Npc) spawn(702700, 187.40462f, 250.03378f, 220.57263f, (byte) 113));
			AncientBox1.add((Npc) spawn(702700, 189.15202f, 227.8461f, 188.0184f, (byte) 104));
			AncientBox1.add((Npc) spawn(702700, 225.0f, 190.0f, 213.7326f, (byte) 88));
			AncientBox1.add((Npc) spawn(702700, 238.0f, 227.0f, 188.69992f, (byte) 72));
			AncientBox1.add((Npc) spawn(702700, 177.7483f, 248.3171f, 207.84833f, (byte) 67));
			AncientBox1.add((Npc) spawn(702700, 220.70958f, 231.82932f, 213.74495f, (byte) 81));
			AncientBox1.add((Npc) spawn(702700, 239.0f, 189.0f, 188.69992f, (byte) 38));
			AncientBox1.add((Npc) spawn(702700, 228.31386f, 202.82614f, 239.83826f, (byte) 53));
			AncientBox1.add((Npc) spawn(702700, 189.0f, 168.0f, 213.73257f, (byte) 77));
			AncientBox1.add((Npc) spawn(702700, 148.0f, 206.0f, 213.73239f, (byte) 60));
			AncientBox1.add((Npc) spawn(702700, 148.89395f, 219.80585f, 187.8586f, (byte) 110));
			AncientBox1.add((Npc) spawn(702700, 256.0f, 206.0f, 214.492f, (byte) 5));
			// Ancient Box 2
			AncientBox2.add((Npc) spawn(702702, 179.30707f, 249.94772f, 240.14725f, (byte) 116));
			AncientBox2.add((Npc) spawn(702702, 168.0f, 246.0f, 189.56546f, (byte) 58));
			AncientBox2.add((Npc) spawn(702702, 147.0f, 253.0f, 213.97218f, (byte) 62));
			AncientBox2.add((Npc) spawn(702702, 194.73796f, 168.25473f, 239.70554f, (byte) 45));
			AncientBox2.add((Npc) spawn(702702, 201.48772f, 207.74902f, 238.82117f, (byte) 78));
			AncientBox2.add((Npc) spawn(702702, 217.0f, 160.0f, 213.896f, (byte) 78));
			AncientBox2.add((Npc) spawn(702702, 255.0f, 213.0f, 188.69994f, (byte) 69));
		}
		/**
		 * Start Mini Game 2
		 */
		else if (zone.getAreaTemplate().getZoneName() == ZoneName.get("TEST_SUBJECT_PRISON_300480000"))
		{
			if (!isStartTimer2)
			{
				isStartTimer2 = true;
				System.currentTimeMillis();
				instance.doOnAllPlayers(player1 ->
				{
					if (player1.isOnline())
					{
						startTestSubjectPrisonTimer();
						PacketSendUtility.sendPacket(player1, new SM_QUEST_ACTION(0, 300));
					}
				});
			}
			TestSubject48012C.add((Npc) spawn(219963, 212.50705f, 509.4579f, 153.22841f, (byte) 114));
			TestSubject48013C.add((Npc) spawn(219964, 225.22668f, 529.2708f, 153.03906f, (byte) 103));
			TestSubject48015C.add((Npc) spawn(219965, 241.73491f, 541.0339f, 152.59001f, (byte) 97));
			TestSubject48023B.add((Npc) spawn(219966, 261.88104f, 545.0504f, 150.50136f, (byte) 90));
			TestSubject48025B.add((Npc) spawn(219967, 295.09076f, 547.3589f, 148.7211f, (byte) 92));
			TestSubject48027B.add((Npc) spawn(219968, 316.75458f, 543.8562f, 148.79962f, (byte) 84));
			TestSubject48039A.add((Npc) spawn(219969, 336.1346f, 532.2977f, 148.47159f, (byte) 75));
			TestSubject48123A.add((Npc) spawn(219978, 345.93323f, 512.3764f, 148.18051f, (byte) 66));
		}
	}
	
	private void raidMysticarium(Npc npc)
	{
		ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			if (!isInstanceDestroyed)
			{
				for (Player player : instance.getPlayersInside())
				{
					npc.setTarget(player);
					((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
					npc.setState(1);
					npc.getMoveController().moveToTargetObject();
					PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
				}
			}
		}, 1000);
	}
	
	/**
	 * Source: https://www.youtube.com/watch?v=ppgwb2p_iA0 https://www.youtube.com/watch?v=D6jy0R-BOnI
	 */
	public void startMysticariumRaid1()
	{
		raidMysticarium((Npc) spawn(219985, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219983, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid2()
	{
		raidMysticarium((Npc) spawn(219986, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid3()
	{
		raidMysticarium((Npc) spawn(219985, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219983, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid4()
	{
		raidMysticarium((Npc) spawn(219986, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid5()
	{
		raidMysticarium((Npc) spawn(219980, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid6()
	{
		raidMysticarium((Npc) spawn(219986, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid7()
	{
		raidMysticarium((Npc) spawn(219985, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219983, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219983, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid8()
	{
		raidMysticarium((Npc) spawn(219980, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid9()
	{
		raidMysticarium((Npc) spawn(219985, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219984, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219984, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid10()
	{
		raidMysticarium((Npc) spawn(219985, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219985, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid11()
	{
		raidMysticarium((Npc) spawn(219981, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219982, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid12()
	{
		raidMysticarium((Npc) spawn(219980, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid13()
	{
		raidMysticarium((Npc) spawn(219985, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219984, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219984, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid14()
	{
		raidMysticarium((Npc) spawn(219980, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid15()
	{
		raidMysticarium((Npc) spawn(219982, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid16()
	{
		raidMysticarium((Npc) spawn(219981, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219985, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219986, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid17()
	{
		raidMysticarium((Npc) spawn(219980, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid18()
	{
		raidMysticarium((Npc) spawn(219984, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219984, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid19()
	{
		raidMysticarium((Npc) spawn(219981, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219982, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219984, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumRaid20()
	{
		raidMysticarium((Npc) spawn(219985, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
		raidMysticarium((Npc) spawn(219986, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	public void startMysticariumBoss()
	{
		raidMysticarium((Npc) spawn(219987, 494.27405f, 491.4183f, 100.36539f, (byte) 106));
	}
	
	private void sendMsg(String str)
	{
		instance.doOnAllPlayers(player -> PacketSendUtility.sendMessage(player, str));
	}
	
	protected void sendMsgByRace(int msg, Race race, int time)
	{
		ThreadPoolManager.getInstance().schedule((Runnable) () -> instance.doOnAllPlayers(player ->
		{
			if (player.getRace().equals(race) || race.equals(Race.PC_ALL))
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(msg));
			}
		}), time);
	}
	
	private void sendMessage(int msgId, long delay)
	{
		if (delay == 0)
		{
			sendMsg(msgId);
		}
		else
		{
			ThreadPoolManager.getInstance().schedule((Runnable) () -> sendMsg(msgId), delay);
		}
	}
	
	private void despawnNpc(Npc npc)
	{
		if (npc != null)
		{
			npc.getController().onDelete();
		}
	}
	
	private void deleteNpc(int npcId)
	{
		if (getNpc(npcId) != null)
		{
			getNpc(npcId).getController().onDelete();
		}
	}
	
	@Override
	public boolean onReviveEvent(Player player)
	{
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
		return TeleportService2.teleportTo(player, mapId, instanceId, 179.34444f, 122.14083f, 231.28636f, (byte) 59);
	}
	
	@Override
	public boolean onDie(Player player, Creature lastAttacker)
	{
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
}