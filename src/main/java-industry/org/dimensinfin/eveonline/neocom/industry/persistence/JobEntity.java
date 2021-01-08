package org.dimensinfin.eveonline.neocom.industry.persistence;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

import org.dimensinfin.eveonline.neocom.database.entities.UpdatableEntity;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdIndustryJobs200Ok;

@DatabaseTable(tableName = "neocom.Jobs")
public class JobEntity extends UpdatableEntity {
	public enum EJobType {
		CCP, NEOCOM
	}

	private static final long serialVersionUID = -6841505320348309318L;
	@DatabaseField(id = true)
	private int jobId = -1;
	@DatabaseField
	private String jobType = EJobType.CCP.name();
	@DatabaseField
	private int installerId = -1;
	@Expose
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

	// - C O N S T R U C T O R S
	public JobEntity() {
		super();
	}

	// - G E T T E R S   &   S E T T E R S
	public int getInstallerId() {
		return this.installerId;
	}

	protected JobEntity setInstallerId( final int installerId ) {
		this.installerId = installerId;
		return this;
	}

	public String getInstallerName() {
		return this.installerName;
	}

	public JobEntity setInstallerName( final String installerName ) {
		this.installerName = installerName;
		return this;
	}

	public int getJobId() {
		return this.jobId;
	}

	protected JobEntity setJobId( final Integer jobId ) {
		this.jobId = jobId;
		return this;
	}
}