package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import es.samiralkalii.myapps.data.teammanagement.IRemoteTeamManagementDatasource
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.NotifType
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.domain.notification.Reply
import es.samiralkalii.myapps.domain.teammanagement.*
import es.samiralkalii.myapps.soporteit.ui.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.ArrayList


private const val TEAM_DOCUMENT_REF= "team"
private const val NOTIFICATION_REF= "notifications"
private const val KEY_NOTIF_SENDER= "sender"
private const val KEY_TYPE= "type"
private const val KEY_VALUE= "value"
private const val KEY_DATE= "date"
private const val KEY_DESTINATION= "destination"
private const val KEY_INVITATION_NOTIF_TYPE= "invitation_to_team"
private const val KEY_USER= "user"
private const val VALUE_USER= "user"
private const val VALUE_GROUP= "group"
private const val TEAM_REF= "teams"
const val NOTIFS_SENT= "notifsSent"
const val NOTIFS_RECEIVED= "notifsReceived"
private const val IN_PROGRESS= "in_progress"
private const val MEMBERS_FIELD= "members"
const val REF_MANAGERS= "managers"
const val REF_HOLIDAYS= "holidays"


private const val AREAS_REF= "areas"
private const val KEY_NAME= "name"
private const val KEY_CATEGORY_LEVEL= "level"
private const val DEPARTMENTS_REF= "departments"
private const val BOSS_CATEGORIES_REF= "bossCategories"
private const val HOLIDAY_DAYS_REF= "holidayDays"
private const val PROFILES_REF= "profiles"
private const val GROUPS_REF= "groups"



class RemoteTeamDatasourceManager(val fstore: FirebaseFirestore): IRemoteTeamManagementDatasource {

    private val logger= LoggerFactory.getLogger(RemoteTeamDatasourceManager::class.java)

    override suspend fun addTeam(team: Team): String {
        team.members?.add(team.boss)
        val newTeamRef= fstore.collection(TEAM_REF).document()
        team.id= newTeamRef.id
        newTeamRef.set(team)
        return newTeamRef.id
    }

    override suspend fun isTeamAlreadyExists(team: Team): Boolean {
        val result= fstore.collection(TEAM_REF).whereEqualTo(KEY_TEAM_NAME_INSENSITIVE, team.nameInsensitive).get().await()
        return result!= null && result.size()>= 1
    }

    override suspend fun getAllUsersButBosesAndNoTeam(): List<User> {
        val result= fstore.collection(USERS_REF).whereEqualTo(KEY_BOSS_LEVEL, 0)
            .whereEqualTo(KEY_IS_EMAIL_VERIFIED, true)
            .whereGreaterThanOrEqualTo(KEY_TEAM_ID, "")
            .whereLessThanOrEqualTo(KEY_TEAM_ID, "")
            .get(Source.SERVER).await()
        val methodResult= ArrayList<User>()
        result.forEach { document ->
            val user: User= document.toObject(User::class.java)
            logger.debug(user.toString())
            methodResult.add(user)
        }
        return methodResult
    }

    override suspend fun inviteUserToTeam(sender: User, destination: User) {

        val notification= Notification(
            type= NotifType.ACTION_INVITE_TEAM,
            sender = sender.id,
            senderName = sender.name,
            senderEmail = sender.email,
            senderProfileImage = sender.remoteProfileImage,
            destination = destination.id
            //team = sender.team,
            //teamId = sender.teamId
        )
        fstore.runTransaction { _ ->
            val notifRef= fstore.collection(NOTIFICATION_REF).document()
            //we add a notification to top level notifications
            notification.id= notifRef.id
            notifRef.set(notification)
            fstore.collection(USERS_REF).document(sender.id)
                .collection(NOTIFS_SENT).document(notification.id).set(notification)
            fstore.collection(USERS_REF).document(destination.id)
                .collection(NOTIFS_RECEIVED).document(notification.id).set(notification)
            fstore.collection(USERS_REF).document(destination.id).update(mapOf(
                KEY_TEAM to notification.team,
                KEY_TEAM_ID to notification.teamId,
                KEY_BOSS to notification.sender,
                KEY_TEAM_INVITATION_STATE to Reply.PENDING
            ))

        }.await()

    }

    override suspend fun addUserToTeam(user: User) {
        /*val refMembers= fstore.collection(TEAM_REF).document(user.teamId)
        refMembers.update(MEMBERS_FIELD, FieldValue.arrayUnion(user.id))*/
    }

    override suspend fun getAreasDepartments(): AreasDepartments {
        val areasDepartments= mutableMapOf<Area, List<Department>>()
        val areasResult= fstore.collection(AREAS_REF).get(Source.SERVER).await()
        if (!areasResult.isEmpty) {
            for (areaDocument in areasResult) {
                val area= areaDocument.data.get(KEY_NAME) as String
                val departments= mutableListOf<Department>()
                val departmentsResult= fstore.collection(AREAS_REF).document(areaDocument.id).collection(DEPARTMENTS_REF).get(Source.SERVER).await()
                if (!departmentsResult.isEmpty) {
                    for (departmentDocument in departmentsResult) {
                        val department= departmentDocument.data.get(KEY_NAME) as String
                        departments.add(
                            Department(
                                departmentDocument.id,
                                department
                            )
                        )
                    }
                    areasDepartments[Area(
                        areaDocument.id,
                        area
                    )]= departments
                }
            }
        }
        return AreasDepartments(
            areasDepartments
        )
    }

    override suspend fun getBossCategorties(): BossCategories {
        val bossCategories= mutableListOf<BossCategory>()
        val categoriesResult= fstore.collection(BOSS_CATEGORIES_REF).get(Source.SERVER).await()
        if (!categoriesResult.isEmpty) {
            for (categoryDocument in categoriesResult) {
                val categoryName = categoryDocument.data[KEY_NAME] as String
                val categoryLevel = (categoryDocument.data[KEY_CATEGORY_LEVEL] as Long).toInt()
                bossCategories.add(
                    BossCategory(
                        categoryDocument.id,
                        categoryName,
                        categoryLevel
                    )
                )
            }
        }
        return BossCategories(
            bossCategories
        )
    }

    override suspend fun isBossAlreadyExist(
        areaId: String,
        departmentId: String,
        bossLevel: Int
    ): Boolean {
        val result= fstore.collection(REF_MANAGERS)
            .whereEqualTo(KEY_BOSS_LEVEL, bossLevel)
            .whereEqualTo(KEY_DEPARTMENT_ID, departmentId)
            .whereEqualTo(KEY_AREA_ID, areaId)
            .get(Source.SERVER).await()
        return !result.isEmpty

    }

    override suspend fun getHolidayDays(): Holidays {
        val holidaysResult= fstore.collection(REF_HOLIDAYS).get(Source.SERVER).await()
        if (!holidaysResult.isEmpty) {
            val holidayDoc= holidaysResult.documents[0]
            return Holidays((holidayDoc.data?.get(HOLIDAY_DAYS_REF) as Long).toInt())
        }
        return Holidays(-1)
    }

    override suspend fun getMyGroups(user: User): GroupList {
        var membersQuery: Query? = null
        if (user.isBoss && user.bossConfirmation== SI) {
            membersQuery= fstore.collection(USERS_REF)
                .whereEqualTo(KEY_IS_EMAIL_VERIFIED, true)
                .whereEqualTo(KEY_AREA_ID, user.areaId)
                .whereEqualTo(KEY_DEPARTMENT_ID, user.departmentId)
        } else if (!user.isBoss && user.membershipConfirmation== SI){
            membersQuery= fstore.collection(USERS_REF)
                .whereEqualTo(KEY_IS_EMAIL_VERIFIED, true)
                .whereEqualTo(KEY_AREA_ID, user.areaId)
                .whereEqualTo(KEY_DEPARTMENT_ID, user.departmentId)
                .whereEqualTo(KEY_MEMBERSHIP_CONFIRMATION, SI)
        }
        membersQuery?.let {
            val membersQueryresult= it.get(Source.SERVER).await()
            if (!membersQueryresult.isEmpty) {
                //Until now we have all members that belongs to user.department && user.area
                val groupAll= membersQueryresult.documents.map { it.data!!.toUser() }.filter { it!= null && it.id!= user.id }.let { Group(id = "TODOS", name = "Todos", members = it as List<User>, area = user.areaId, department = user.departmentId) }
                var otherGroups= getTheGroupsIbelongTo(user, groupAll.members).filter { it.members!= null && !it.members.isEmpty() }
                return GroupList(listOf(groupAll, *otherGroups.toTypedArray()))
            }
        }
        return GroupList(listOf())
    }

    suspend private fun getTheGroupsIbelongTo(user: User, users: List<User>): List<Group> {
        val groups= fstore.collection(GROUPS_REF)
            .whereEqualTo(KEY_AREA_ID, user.areaId)
            .whereEqualTo(KEY_DEPARTMENT_ID, user.departmentId)
            .whereArrayContains(MEMBERS_FIELD, user.id).get(Source.SERVER).await()
        if (!groups.isEmpty) {
            val groups= groups.documents.map { doc ->
                with(doc) {
                    val groupName= data!![KEY_GROUP_NAME] as String
                    val id= data!![KEY_GROUP_ID] as String
                    val members= data!![KEY_GROUP_MEMBERS] as List<String>
                    val groupUsers= members.filter { it!= null && it!= user.id }.map { it ->
                        users.find { user-> user.id== it }
                    }.filter { it!= null }
                    Group(id, groupName, members = groupUsers as List<User>, area = user.areaId, department = user.departmentId)
                }
            }
            return groups
        }
        return listOf()
    }

    override suspend fun confirmDenyMember(user: String, isConfirmed: Boolean, profile: String,
                                           profileId: String, holidayDays: Int, internal: Boolean) {
        if (!isConfirmed) {
            fstore.collection(USERS_REF).document(user).update(
                mapOf( KEY_MEMBERSHIP_CONFIRMATION to NO, KEY_MEMBERSHIP_CONFIRMED_AT to formatDate(Date().time))).await()
        } else {
            fstore.collection(USERS_REF).document(user).update(
                mapOf( KEY_MEMBERSHIP_CONFIRMATION to SI, KEY_MEMBERSHIP_CONFIRMED_AT to formatDate(Date().time),
                KEY_PROFILE to profile, KEY_PROFILE_ID to profileId, KEY_INTERNAL_EMPLOYEE to internal,
                KEY_HOLIDAY_DAYS to holidayDays.toLong())).await()
        }
    }

    override suspend fun getMemberConfirmationData(user: String): Map<String, Any?> {
        val confirmationData= mutableMapOf<String, Any?>()
        val result= fstore.collection(USERS_REF).document(user).get().await()
        if (result!= null && result.data!= null) {
            val data= result.data!!
            confirmationData[KEY_MEMBERSHIP_CONFIRMED_AT]= data[KEY_MEMBERSHIP_CONFIRMED_AT]
            confirmationData[KEY_PROFILE]= data[KEY_PROFILE]
            confirmationData[KEY_PROFILE_ID]= data[KEY_PROFILE_ID]
            confirmationData[KEY_INTERNAL_EMPLOYEE]= data[KEY_INTERNAL_EMPLOYEE]
            confirmationData[KEY_HOLIDAY_DAYS]= (data[KEY_HOLIDAY_DAYS] as Long).toInt()
        }
        return confirmationData
    }

    override suspend fun getProfiles(area: String): Profiles {
        //val profiles= mutableListOf<Profile>()
        val profilesDocs= fstore.collection(AREAS_REF).document(area).collection(PROFILES_REF).get(Source.SERVER).await()
        if (!profilesDocs.isEmpty()) {
            return Profiles(profilesDocs.documents.map { it.id to it.toObject(Profile::class.java)!!}.map { Profile(it.first, it.second.name, it.second.level) })
        }
        return Profiles(listOf())

    }

}