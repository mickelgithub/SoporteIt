package es.samiralkalii.myapps.soporteit.di

import es.samiralkalii.myapps.usecase.authlogin.*
import es.samiralkalii.myapps.usecase.messaging.HandleTeamInvitationUseCase
import es.samiralkalii.myapps.usecase.messaging.NotifyMessagingUseCase
import es.samiralkalii.myapps.usecase.messaging.RegisterMessagingTokenUseCase
import es.samiralkalii.myapps.usecase.notification.DeleteNotificationUseCase
import es.samiralkalii.myapps.usecase.notification.GetNotificationsUseCase
import es.samiralkalii.myapps.usecase.notification.ReplyNotificationUseCase
import es.samiralkalii.myapps.usecase.notification.UpdateNotificationStateUseCase
import es.samiralkalii.myapps.usecase.teammanagement.AddTeamUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetAllUsersButBosesAndNoTeamUseCase
import es.samiralkalii.myapps.usecase.teammanagement.InviteUserUseCase
import org.koin.dsl.module


val useCaseModule= module {
    factory { CheckUserAuthUseCase(get(), get(), get()) }
    factory { LogupUseCase(get(), get(), get(), get(), get()) }
    factory { LoginUserCase(get(), get(), get(), get(), get()) }
    factory { Compare2ImageProfileUseCase(get()) }
    factory { SaveProfileChangeUseCase(get(), get(), get(), get()) }
    factory { RegisterMessagingTokenUseCase(get(), get()) }
    factory { NotifyMessagingUseCase(get(), get(), get(), get()) }
    factory { AddTeamUseCase(get(), get(), get()) }
    factory { GetAllUsersButBosesAndNoTeamUseCase(get()) }
    factory { InviteUserUseCase(get(), get()) }
    factory { HandleTeamInvitationUseCase(get(), get(), get(), get(), get()) }
    factory { GetNotificationsUseCase(get()) }
    factory { UpdateNotificationStateUseCase(get()) }
    factory { DeleteNotificationUseCase(get()) }
    factory { ReplyNotificationUseCase(get()) }
}