package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotNull;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.dimensinfin.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.database.core.ISDEDatabaseService;
import org.dimensinfin.eveonline.neocom.database.core.ISDEStatement;
import org.dimensinfin.eveonline.neocom.domain.Resource;
import org.dimensinfin.eveonline.neocom.service.DMServicesDependenciesModule;
import org.dimensinfin.eveonline.neocom.service.ResourceFactory;
import org.dimensinfin.logging.LogWrapper;

public class SDERepository {
	private final ISDEDatabaseService sdeDatabaseService;
	private final ResourceFactory resourceFactory;

	// - C O N S T R U C T O R S
	@Inject
	public SDERepository( final @NotNull @Named(DMServicesDependenciesModule.ISDE_DATABASE_SERVICE) ISDEDatabaseService sdeDatabaseService,
	                      final @NotNull @Named(DMServicesDependenciesModule.RESOURCE_FACTORY) ResourceFactory resourceFactory ) {
		this.sdeDatabaseService = Objects.requireNonNull( sdeDatabaseService );
		this.resourceFactory = Objects.requireNonNull( resourceFactory );
	}

	/**
	 * Generates the list of materials required to manufacture an item. The data comes from the SDE repository and it is the list of required
	 * resources as set on the SDE list, no skill not optimizations are applied.
	 * The list adds the skill books and skill levels required to use or build the item.
	 */
	@TimeElapsed
	public List<Resource> accessBillOfMaterials( final int itemId ) {
		LogWrapper.enter();
		final String SELECT_MATERIAL_USAGE = "SELECT typeID, materialTypeID, quantity " +
				"FROM industryActivityMaterials " +
				"WHERE typeID = ? AND activityID = 1";
		final int BOM_USAGE_TYPEID_COLINDEX = 1;
		final int BOM_USAGE_MATERIAL_TYPEID_COLINDEX = 2;
		final int BOM_USAGE_QUANTITY_COLINDEX = 3;
		final List<Resource> buildJob = new ArrayList<>();
		try {
			final ISDEStatement cursor = this.sdeDatabaseService
					.constructStatement( SELECT_MATERIAL_USAGE, new String[]{ Integer.valueOf( itemId ).toString() } );
			int blueprintId = -1; // Add a blueprint detection to detect BOM for blueprints.
			while (cursor.moveToNext()) {
				int materialTypeID = cursor.getInt( BOM_USAGE_MATERIAL_TYPEID_COLINDEX );
				int qty = cursor.getInt( BOM_USAGE_QUANTITY_COLINDEX );
				blueprintId = cursor.getInt( BOM_USAGE_TYPEID_COLINDEX );
				Resource resource = this.resourceFactory.generateResource4Id( materialTypeID, qty );
				buildJob.add( resource );
			}
			cursor.close();
			if (blueprintId != -1) {
				buildJob.add( this.resourceFactory
						.generateResource4Id( blueprintId, 1 ) ); // We have collected the required blueprint. Add it to the list of resources.
			}
			buildJob.addAll( this.accessSkillRequired( itemId ) ); // Add the list of skills required.
		} catch (final SQLException sqle) {
			LogWrapper.error( sqle );
		} finally {
			LogWrapper.exit();
		}
		return buildJob;
	}

	public int accessModule4Blueprint( final int blueprintTypeId ) {
		LogWrapper.enter();
		final String SELECT_MODULE4BLUEPRINT = "SELECT productTypeID FROM industryActivityProducts BT"
				+ " WHERE typeId = ? AND activityID = 1";
		final int PRODUCTTYPEID_MODULE4BLUEPRINT_COLINDEX = 1;
		int productTypeId = -1;
		try {
			final ISDEStatement cursor = this.sdeDatabaseService
					.constructStatement( SELECT_MODULE4BLUEPRINT, new String[]{ Integer.toString( blueprintTypeId ) } );
			while (cursor.moveToNext()) {
				productTypeId = cursor.getInt( PRODUCTTYPEID_MODULE4BLUEPRINT_COLINDEX );
			}
			cursor.close();
		} catch (final SQLException sqle) {
			LogWrapper.error( sqle );
		} finally {
			LogWrapper.exit();
		}
		return productTypeId;
	}

	public List<Resource> accessSkillRequired( final int itemId ) {
		LogWrapper.enter();
		final String SKILLS_REQUIRED = "SELECT ais.typeID, ais.skillID, ais.level, it.typeName " +
				"FROM industryActivitySkills ais, invTypes it " +
				"WHERE ais.typeID = ? AND ais.activityID = 1 AND it.typeID=ais.skillID";
		final int SKILL_REQUIRED_SKILLID_COLINDEX = 2;
		final int SKILL_REQUIRED_LEVEL_COLINDEX = 3;
		final List<Resource> skillsRequired = new ArrayList<>();
		try {
			final ISDEStatement cursor = this.sdeDatabaseService
					.constructStatement( SKILLS_REQUIRED, new String[]{ Integer.toString( itemId ) } );
			while (cursor.moveToNext()) {
				skillsRequired.add( this.resourceFactory.generateResource4Id(
						cursor.getInt( SKILL_REQUIRED_SKILLID_COLINDEX ),
						cursor.getInt( SKILL_REQUIRED_LEVEL_COLINDEX ) ) );
			}
			cursor.close();
		} catch (final SQLException sqle) {
			LogWrapper.error( sqle );
		} finally {
			LogWrapper.exit();
		}
		return skillsRequired;
	}
}