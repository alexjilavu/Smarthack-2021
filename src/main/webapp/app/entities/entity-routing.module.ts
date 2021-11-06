import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'app-user',
        data: { pageTitle: 'smarthackApp.appUser.home.title' },
        loadChildren: () => import('./app-user/app-user.module').then(m => m.AppUserModule),
      },
      {
        path: 'challenge',
        data: { pageTitle: 'smarthackApp.challenge.home.title' },
        loadChildren: () => import('./challenge/challenge.module').then(m => m.ChallengeModule),
      },
      {
        path: 'hash-tag',
        data: { pageTitle: 'smarthackApp.hashTag.home.title' },
        loadChildren: () => import('./hash-tag/hash-tag.module').then(m => m.HashTagModule),
      },
      {
        path: 'post',
        data: { pageTitle: 'smarthackApp.post.home.title' },
        loadChildren: () => import('./post/post.module').then(m => m.PostModule),
      },
      {
        path: 'company',
        data: { pageTitle: 'smarthackApp.company.home.title' },
        loadChildren: () => import('./company/company.module').then(m => m.CompanyModule),
      },
      {
        path: 'reward',
        data: { pageTitle: 'smarthackApp.reward.home.title' },
        loadChildren: () => import('./reward/reward.module').then(m => m.RewardModule),
      },
      {
        path: 'icon',
        data: { pageTitle: 'smarthackApp.icon.home.title' },
        loadChildren: () => import('./icon/icon.module').then(m => m.IconModule),
      },
      {
        path: 'transaction',
        data: { pageTitle: 'smarthackApp.transaction.home.title' },
        loadChildren: () => import('./transaction/transaction.module').then(m => m.TransactionModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
