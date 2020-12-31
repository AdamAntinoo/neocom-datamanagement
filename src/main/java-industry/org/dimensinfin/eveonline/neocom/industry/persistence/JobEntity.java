package org.dimensinfin.eveonline.neocom.industry.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.database.entities.UpdatableEntity;
import org.dimensinfin.eveonline.neocom.domain.NeoComNode;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;

@DatabaseTable(tableName = "neocom.Jobs")
@JsonIgnoreProperties
public class JobEntity extends UpdatableEntity {
	public enum EJobType {
		CCP, NEOCOM
	}

	private static final long serialVersionUID = -6841505320348309318L;
	private static final int maxRuns = 10;
	private static Logger logger = LoggerFactory.getLogger( "Job" );
	@DatabaseField(id = true)
	private int jobId = -1;
	@DatabaseField
	private String jobType = EJobType.CCP.name();
	@DatabaseField
	private int installerId = -1;
	@DatabaseField
	private String installerName;
	@DatabaseField
	private long facilityId = -3;
	@DatabaseField(index = true)
	private long stationId = -2;
	@DatabaseField
	private int activityId = 0;
	@DatabaseField(index = true)
	private long blueprintId = -1;
	@DatabaseField
	private int blueprintTypeId = -1;
	@DatabaseField(index = true)
	private long blueprintLocationId = -2;
	@DatabaseField
	private long outputLocationId = -2;
	@DatabaseField
	private int runs = 0;
	@DatabaseField
	private double cost = 0.0;
	@DatabaseField
	private int licensedRuns = 1;
	@DatabaseField
	private float probability = 0f;
	@DatabaseField
	private int productTypeId;
	@DatabaseField(dataType = DataType.ENUM_STRING)
	private GetCharactersCharacterIdIndustryJobs200Ok.StatusEnum status = null;
	@DatabaseField
	private int duration = 1;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private DateTime startDate = null;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private DateTime endDate = null;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private DateTime pauseDate = null;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private DateTime completedDate = null;
	@DatabaseField
	private int completedCharacterId = -1;
	@DatabaseField
	private int successfulRuns = 0;

	public JobEntity() {
		super();
	}

	public int getJobId() {
		return this.jobId;
	}

	public JobEntity setInstallerName( final String installerName ) {
		this.installerName = installerName;
		return this;
	}

	public int getInstallerId() {
		return this.installerId;
	}

	public String getInstallerName() {
		return this.installerName;
	}
	//	public JobEntity() {
	//		jsonClass = "Job";
	//	}

	//	public JobEntity( final int newJobId ) {
	//		this();
	//		jobId = newJobId;
	//		try {
	//			Dao<Job, String> jobDao = accessGlobal().getNeocomDBHelper().getJobDao();
	//			// Try to create the record. It fails then it was already created.
	//			jobDao.createOrUpdate(this);
	//		} catch (final SQLException sqle) {
	//			logger.info("WR [TimeStamp.<constructor>]> Industry Job exists. Update values.");
	//			this.store();
	//		}
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//	public int getActivityId() {
	//		return activityId;
	//	}
	//
	//	public long getBlueprintId() {
	//		return blueprintId;
	//	}
	//
	//	public long getBlueprintLocationId() {
	//		return blueprintLocationId;
	//	}
	//
	//	public String getBlueprintName() {
	//		try {
	//			if (null == blueprintItem) {
	//				blueprintItem = accessGlobal().searchItem4Id(blueprintTypeId);
	//			}
	//		} catch (NeocomRuntimeException neoe) {
	//			blueprintItem = new EveItem();
	//		}
	//		return blueprintItem.getName();
	//	}
	//
	//	public int getDuration() {
	//		return duration;
	//	}
	//
	//	public int getBlueprintTypeId() {
	//		return blueprintTypeId;
	//	}
	//
	//	public long getCompletedCharacterId() {
	//		return completedCharacterId;
	//	}
	//
	//	public DateTime getCompletedDate() {
	//		return completedDate;
	//	}
	//
	//	public double getCost() {
	//		return cost;
	//	}
	//
	//	public DateTime getEndDate() {
	//		return endDate;
	//	}
	//
	//	public long getFacilityId() {
	//		return facilityId;
	//	}
	//
	//	public long getJobId() {
	//		return jobId;
	//	}
	//
	//	public EveLocation getJobLocation() {
	//		try {
	//			if (null == jobLocation) {
	//				jobLocation = accessGlobal().searchLocation4Id(facilityId);
	//			}
	//		} catch (NeocomRuntimeException neoe) {
	//			jobLocation = new EveLocation();
	//		}
	//		return jobLocation;
	//	}
	//
	//	public String getJobType() {
	//		return jobType;
	//	}
	//
	//	public int getLicensedRuns() {
	//		return licensedRuns;
	//	}
	//
	//	public int getMaxRuns() {
	//		return Job.maxRuns;
	//	}
	//
	//	public EveItem getBlueprintItem() {
	//		return blueprintItem;
	//	}
	//
	//	public EveItem getProductItem() {
	//		return productItem;
	//	}
	//
	//	public NeoComNode getJobOutputLocation() {
	//		return jobOutputLocation;
	//	}
	//
	//	public int getProductTypeId() {
	//		return productTypeId;
	//	}
	//
	//	public int getRuns() {
	//		return runs;
	//	}
	//
	//	public DateTime getStartDate() {
	//		return startDate;
	//	}
	//
	//	public long getStationId() {
	//		return stationId;
	//	}
	//
	//	public GetCharactersCharacterIdIndustryJobs200Ok.StatusEnum getStatus() {
	//		return status;
	//	}
	//
	//	public int getSuccessfulRuns() {
	//		return successfulRuns;
	//	}
	//
	////	public int getTimeInSeconds() {
	////		return timeInSeconds;
	////	}
	//
	//	public Job setJobId( final int jobId ) {
	//		this.jobId = jobId;
	//		return this;
	//	}
	//
	//	public Job setInstallerId( final int installerId ) {
	//		this.installerId = installerId;
	//		return this;
	//	}
	//
	//	public Job setActivityId( final int activityId ) {
	//		this.activityId = activityId;
	//		return this;
	//	}
	//
	//	public Job setBlueprintId( final long blueprintId ) {
	//		this.blueprintId = blueprintId;
	//		return this;
	//	}
	//
	//	public Job setBlueprintLocationId( final long blueprintLocationId ) {
	//		try {
	//			this.blueprintLocationId = blueprintLocationId;
	//			// Cache the location pointed bu this identifier.
	//			blueprintLocation = accessGlobal().searchLocation4Id(blueprintLocationId);
	//		} catch (NeocomRuntimeException neoe) {
	//			blueprintLocation = new EveLocation();
	//		}
	//		return this;
	//	}
	//
	//	public Job setBlueprintTypeId( final int blueprintTypeId ) {
	//		try {
	//			this.blueprintTypeId = blueprintTypeId;
	//			// Load the blueprint item reference.
	//			blueprintItem = accessGlobal().searchItem4Id(blueprintTypeId);
	//			// Calculate the resulting item type.
	//			productTypeId = accessGlobal().searchModule4Blueprint(blueprintTypeId);
	//			productItem = accessGlobal().searchItem4Id(productTypeId);
	//		} catch (NeocomRuntimeException neoe) {
	//			blueprintItem = new EveItem();
	//			productTypeId = 34;
	//			productItem = new EveItem();
	//		}
	//		return this;
	//	}
	//
	//	public Job setOutputLocationId( final long outputLocationId ) {
	//		this.outputLocationId = outputLocationId;
	//		try {
	//			// Load the output location item reference.
	//			final EveLocation ouputLocation = accessGlobal().searchLocation4Id(outputLocationId);
	//			if (ouputLocation.getTypeId() == ELocationType.UNKNOWN) {
	//				// If the output location is UNKNOWN then this should be a reachable item. Search for it.
	//				jobOutputLocation = accessGlobal().getNeocomDBHelper()
	//						.getAssetDao().queryForEq("assetId", outputLocationId).get(0);
	//			} else jobOutputLocation = ouputLocation;
	//		} catch (SQLException sqle) {
	//			sqle.printStackTrace();
	//		} catch (NeocomRuntimeException neoe) {
	//			jobOutputLocation = new EveLocation();
	//		}
	//		return this;
	//	}
	//
	//	public void setCompletedCharacterId( final int completedCharacterId ) {
	//		this.completedCharacterId = completedCharacterId;
	//	}
	//
	//	public void setCompletedDate( final DateTime completedDate ) {
	//		this.completedDate = completedDate;
	//	}
	//
	//	public Job setCost( final double cost ) {
	//		this.cost = cost;
	//		return this;
	//	}
	//
	//	public Job setEndDate( final DateTime endDate ) {
	//		this.endDate = endDate;
	//		return this;
	//	}
	//
	//	public Job setDuration( final int durationnumber ) {
	//		this.duration = durationnumber;
	//		return this;
	//	}
	//
	//	public Job setInstallerID( final int installerID ) {
	//		installerId = installerID;
	////		ownerId = installerID;
	//		return this;
	//	}
	//
	//	public Job setFacilityId( final long facilityId ) {
	//		this.facilityId = facilityId;
	//		return this;
	//	}
	//
	//	public void setJobType( final String jobType ) {
	//		this.jobType = jobType;
	//	}
	//
	//	public Job setLicensedRuns( final int licensedRuns ) {
	//		this.licensedRuns = licensedRuns;
	//		return this;
	//	}
	//
	////	public void setOwnerId( final long ownerId ) {
	////		this.ownerId = ownerId;
	////	}
	//
	////	public void setProductTypeId( final int productTypeId ) {
	////		this.productTypeId = productTypeId;
	////	}
	//
	//	public Job setRuns( final int newRuns ) {
	//		runs = newRuns;
	//		return this;
	//	}
	//
	//	public Job setStartDate( final DateTime startDate ) {
	//		this.startDate = startDate;
	//		return this;
	//	}
	//
	//	public Job setStationId( final long stationId ) {
	//		this.stationId = stationId;
	//		return this;
	//	}
	//
	//	public Job setStatus( final GetCharactersCharacterIdIndustryJobs200Ok.StatusEnum status ) {
	//		this.status = status;
	//		return this;
	//	}
	//
	//	public void setSuccessfulRuns( final int successfulRuns ) {
	//		this.successfulRuns = successfulRuns;
	//	}
	//
	////	public void setTimeInSeconds( final int timeInSeconds ) {
	////		this.timeInSeconds = timeInSeconds;
	////	}
	//
	//	public Job store() {
	//		try {
	//			Dao<Job, String> jobDao = accessGlobal().getNeocomDBHelper().getJobDao();
	//			// Try to create the record. It fails then it was already created.
	//			jobDao.createOrUpdate(this);
	//		} catch (final SQLException sqle) {
	//			logger.info("WR [TimeStamp.<constructor>]> Industry Job exists. Update values.");
	//			this.store();
	//		}
	//		return this;
	//	}
	//
	//	@Override
	//	public String toString() {
	//		StringBuffer buffer = new StringBuffer("Job [");
	//		buffer.append(jobId).append(" ");
	//		buffer.append("[").append(blueprintId).append("] ");
	//		buffer.append("#").append(blueprintTypeId).append(" ");
	//		buffer.append(this.getBlueprintName()).append(" ");
	//		if (activityId == ModelWideConstants.activities.MANUFACTURING) {
	//			buffer.append("MANUFACTURE").append(" ");
	//		}
	//		if (activityId == ModelWideConstants.activities.INVENTION) {
	//			buffer.append("INVENTION").append(" ");
	//		}
	//		buffer.append("Output:#").append(productTypeId).append(" ");
	//		//		buffer.append("Module [").append(moduleName).append("] ");
	//		buffer.append("Start:").append(startDate).append(" - End:").append(endDate);
	//		buffer.append("Run/Licensed:").append(runs).append("/").append(licensedRuns).append(" ");
	//		buffer.append("]");
	//		return buffer.toString();
	//	}
	// - B U I L D E R
	public static class Builder {
		private final JobEntity onConstruction;

		// - C O N S T R U C T O R S
		public Builder() {
			this.onConstruction = new JobEntity();
		}

		public JobEntity build() {
			return this.onConstruction;
		}
	}
}