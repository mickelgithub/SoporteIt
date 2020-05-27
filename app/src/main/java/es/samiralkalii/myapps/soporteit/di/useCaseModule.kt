package es.samiralkalii.myapps.soporteit.di

import es.samiralkalii.myapps.usecase.authlogin.*
import es.samiralkalii.myapps.usecase.messaging.HandleTeamInvitationUseCase
import es.samiralkalii.myapps.usecase.messaging.NotifyMessagingUseCase
import es.samiralkalii.myapps.usecase.messaging.RegisterMessagingTokenUseCase
import es.samiralkalii.myapps.usecase.notification.DeleteNotificationUseCase
import es.samiralkalii.myapps.usecase.notification.GetNotificationsUseCase
import es.samiralkalii.myapps.usecase.notification.ReplyNotificationUseCase
import es.samiralkalii.myapps.usecase.notification.UpdateNotificationStateUseCase
import es.samiralkalii.myapps.usecase.teammanagement.*
import es.samiralkalii.myapps.usecase.usermanagment.GetUserUseCase
import org.koin.dsl.module


val useCaseModule= module {
    factory { CheckUserAuthUseCase(get(), get(), get()) }
    factory { LogupUseCase(get(), get(), get(), get(), get(), get()) }
    factory { LoginUserCase(get(), get(), get(), get(), get()) }
    factory { Compare2ImageProfileUseCase(get()) }
    factory { UpdateProfileImageUseCase(get(), get(), get(), get()) }
    factory { RegisterMessagingTokenUseCase(get(), get()) }
    factory { NotifyMessagingUseCase(get(), get(), get(), get(), get(), get()) }
    factory { AddTeamUseCase(get(), get(), get()) }
    factory { GetAllUsersButBosesAndNoTeamUseCase(get()) }
    factory { InviteUserUseCase(get(), get()) }
    factory { HandleTeamInvitationUseCase(get(), get(), get(), get(), get()) }
    factory { GetNotificationsUseCase(get()) }
    factory { UpdateNotificationStateUseCase(get()) }
    factory { DeleteNotificationUseCase(get()) }
    factory { ReplyNotificationUseCase(get()) }
    factory { GetAreasDepartmentsUseCase(get()) }
    factory { GetBossCategoriesUseCase(get()) }
    factory { GetHolidayDaysUseCase(get()) }
    factory { GetUserUseCase(get(), get()) }
    factory { GetGroupsUseCase(get()) }
    factory { ConfirmDenyMemberUseCase(get()) }
    factory { GetProfilesUseCase(get()) }
    factory { GetDeparmentUsersUseCase(get()) }

}