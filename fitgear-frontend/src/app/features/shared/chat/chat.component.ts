import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { MockDataService } from '../../../core/services/mock-data.service';
import { ChatMessage } from '../../../core/models/models';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, NavbarComponent],
  template: `
    <div class="flex min-h-screen bg-bg">
      <app-sidebar></app-sidebar>

      <div class="flex-1 flex flex-col min-w-0">
        <app-navbar title="Chat">
          <!-- Online indicator -->
          <div class="flex items-center gap-2 text-sm text-gray-400">
            <div class="w-2 h-2 rounded-full bg-accent"></div>
            {{ otherName }} is online
          </div>
        </app-navbar>

        <div class="flex-1 flex flex-col" style="height: calc(100vh - 65px)">

          <!-- Chat header -->
          <div class="flex items-center gap-4 px-6 py-4 border-b border-white/5 bg-card/50">
            <div class="w-10 h-10 rounded-full bg-accent/20 flex items-center justify-center flex-shrink-0">
              <i class="fa-solid fa-user-tie text-accent"></i>
            </div>
            <div>
              <p class="font-semibold text-white">{{ otherName }}</p>
              <p class="text-xs text-gray-500">Your {{ auth.isClient() ? 'Coach' : 'Client' }}</p>
            </div>
          </div>

          <!-- Messages -->
          <div class="flex-1 overflow-y-auto px-6 py-4 space-y-4" #msgContainer>
            <div *ngFor="let msg of messages()"
                 class="flex animate-fade-in"
                 [class.justify-end]="msg.isOwn"
                 [class.justify-start]="!msg.isOwn">

              <!-- Avatar (others) -->
              <div *ngIf="!msg.isOwn" class="w-8 h-8 rounded-full bg-accent/20 flex items-center justify-center flex-shrink-0 mr-3 self-end">
                <i class="fa-solid fa-user-tie text-accent text-xs"></i>
              </div>

              <div class="max-w-[70%] flex flex-col"
                   [class.items-end]="msg.isOwn"
                   [class.items-start]="!msg.isOwn">
                <div class="px-4 py-2.5 rounded-2xl text-sm leading-relaxed"
                     [class.bg-accent]="msg.isOwn"
                     [class.text-black]="msg.isOwn"
                     [class.font-medium]="msg.isOwn"
                     [class.bg-card-2]="!msg.isOwn"
                     [class.text-gray-200]="!msg.isOwn"
                     [class.rounded-br-sm]="msg.isOwn"
                     [class.rounded-bl-sm]="!msg.isOwn">
                  {{ msg.content }}
                </div>
                <span class="text-xs text-gray-600 mt-1 px-1">{{ msg.timestamp | date:'HH:mm' }}</span>
              </div>

              <!-- Avatar (own) -->
              <div *ngIf="msg.isOwn" class="w-8 h-8 rounded-full bg-card-2 flex items-center justify-center flex-shrink-0 ml-3 self-end">
                <span class="text-xs font-bold text-gray-400">{{ initials }}</span>
              </div>
            </div>

            <!-- Typing indicator -->
            <div *ngIf="typing" class="flex justify-start animate-fade-in">
              <div class="w-8 h-8 rounded-full bg-accent/20 flex items-center justify-center mr-3 self-end">
                <i class="fa-solid fa-user-tie text-accent text-xs"></i>
              </div>
              <div class="bg-card-2 px-4 py-3 rounded-2xl rounded-bl-sm flex items-center gap-1.5">
                <span *ngFor="let d of [0,1,2]" class="w-1.5 h-1.5 rounded-full bg-gray-400 animate-bounce"
                      [style.animation-delay]="d * 150 + 'ms'"></span>
              </div>
            </div>
          </div>

          <!-- Input bar -->
          <div class="border-t border-white/5 px-6 py-4 bg-card/30 backdrop-blur-sm">
            <form (ngSubmit)="sendMessage()" class="flex items-center gap-3">
              <button type="button" class="btn-icon text-gray-500 hover:text-gray-300">
                <i class="fa-solid fa-paperclip"></i>
              </button>
              <input [(ngModel)]="newMsg" name="msg" type="text" class="form-input flex-1 py-2.5"
                     placeholder="Type a message…" (keyup.enter)="sendMessage()">
              <button type="submit" class="btn-primary" [disabled]="!newMsg.trim()">
                <i class="fa-solid fa-paper-plane"></i>
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ChatComponent {
  auth  = inject(AuthService);
  mock  = inject(MockDataService);

  messages = signal<ChatMessage[]>([...this.mock.chatMessages]);
  newMsg   = '';
  typing   = false;

  get otherName() { return this.auth.isClient() ? 'Alex Morgan' : 'Jordan Smith'; }
  get initials()  {
    const u = this.auth.user();
    return u ? ((u.firstName[0] ?? '') + (u.lastName[0] ?? '')).toUpperCase() : 'ME';
  }

  sendMessage(): void {
    if (!this.newMsg.trim()) return;
    const userId = this.auth.user()?.id ?? 0;
    this.messages.update(m => [...m, {
      id: Date.now(), senderId: userId, receiverId: userId === 1 ? 2 : 1,
      content: this.newMsg.trim(), timestamp: new Date().toISOString(), isOwn: true,
    }]);
    this.newMsg = '';
    // Mock reply after delay
    this.typing = true;
    setTimeout(() => {
      const replies = [
        'Got it! I will update your plan accordingly.',
        'Great work! Keep pushing! 💪',
        'Noted. Let me check your progress data.',
        'Awesome consistency this week!',
        'I will prepare your new workout for next week.',
      ];
      this.typing = false;
      this.messages.update(m => [...m, {
        id: Date.now() + 1, senderId: userId === 1 ? 2 : 1, receiverId: userId,
        content: replies[Math.floor(Math.random() * replies.length)],
        timestamp: new Date().toISOString(), isOwn: false,
      }]);
    }, 1800);
  }
}
