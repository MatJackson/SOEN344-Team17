import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './components/login-registration/login/login.component';
import { SiteComponent } from './components/shared/site/site.component';
import { PatientComponent } from './components/patients/patient/patient.component';
import { DoctorComponent } from './components/doctors/doctor/doctor.component';
import { NurseComponent } from './components/nurses/nurse/nurse.component';
import { DoctorAuthenticationGuard } from './guards/doctor-authentication.guard';
import { PatientAuthenticationGuard } from './guards/patient-authentication.guard';
import { NurseAuthenticationGuard } from './guards/nurse-authentication.guard';

const routes: Routes = [
  {path: '', component: SiteComponent,
  //will need guards
  children: [
    { path: 'patient', canActivate: [PatientAuthenticationGuard], component: PatientComponent},
    { path: 'doctor', canActivate: [DoctorAuthenticationGuard], component: DoctorComponent},
    { path: 'nurse', canActivate: [NurseAuthenticationGuard], component: NurseComponent}
  ]},
  {path: 'login', component: LoginComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }