import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { IconComponent } from '../../shared/components/icon/icon.component';
import { IcIconName } from '../../shared/icons/icons';

interface FeatureCard {
  icon: IcIconName;
  title: string;
  description: string;
}

interface HeroFeature {
  icon: IcIconName;
  title: string;
  subtitle: string;
  bgClass: string;
  iconClass: string;
}

interface TrustItem {
  icon: IcIconName;
  label: string;
}

interface Pillar {
  icon: IcIconName;
  iconBg: string;
  iconColor: string;
  titleColor: string;
  title: string;
  desc: string;
}

interface AccessFeature {
  icon: IcIconName;
  label: string;
}

interface Stat {
  value: string;
  label: string;
}

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterLink, IconComponent],
  templateUrl: './landing.component.html',
})
export class LandingComponent {
  readonly year = new Date().getFullYear();

  readonly stats: Stat[] = [
    { value: '100%', label: 'Accessibility-first design' },
    { value: 'Real-time', label: 'Messaging & notifications' },
    { value: 'Secure', label: 'JWT auth & privacy ready' },
  ];

  readonly heroFeatures: HeroFeature[] = [
    {
      icon: 'users',
      title: 'Diverse professionals',
      subtitle: 'Network with talent & inclusive employers',
      bgClass: 'bg-primary-50/70 hover:bg-primary-50',
      iconClass: 'bg-primary',
    },
    {
      icon: 'briefcase',
      title: 'Accessible careers',
      subtitle: 'Jobs with workplace inclusion notes',
      bgClass: 'bg-accent-50/70 hover:bg-accent-50',
      iconClass: 'bg-accent',
    },
    {
      icon: 'message',
      title: 'Direct messaging',
      subtitle: 'Talk with recruiters in real time',
      bgClass: 'bg-secondary-50/70 hover:bg-secondary-50',
      iconClass: 'bg-secondary',
    },
  ];

  readonly avatarColors = [
    { letter: 'S', bg: 'bg-primary' },
    { letter: 'A', bg: 'bg-accent' },
    { letter: 'M', bg: 'bg-secondary' },
  ];

  readonly trustItems: TrustItem[] = [
    { icon: 'check', label: 'WCAG-compliant interface' },
    { icon: 'accessibility', label: 'Full keyboard navigation' },
    { icon: 'bell', label: 'Real-time notifications' },
    { icon: 'users', label: 'Role-based access control' },
    { icon: 'send', label: 'End-to-end messaging' },
    { icon: 'admin', label: 'Enterprise-grade security' },
  ];

  readonly features: FeatureCard[] = [
    {
      icon: 'user',
      title: 'Professional profiles',
      description: 'Showcase skills, experience, education, projects, and accessibility preferences with confidence.',
    },
    {
      icon: 'briefcase',
      title: 'Job search & apply',
      description: 'Discover roles with accessibility notes, apply in minutes, and track every application.',
    },
    {
      icon: 'network',
      title: 'Meaningful networking',
      description: 'Find professionals, send connection requests, and grow a supportive career network.',
    },
    {
      icon: 'message',
      title: 'Real-time messaging',
      description: 'Chat with connections and recruiters with live delivery and a clean conversation UI.',
    },
    {
      icon: 'building',
      title: 'Company pages',
      description: 'Employers showcase brand, culture, and open roles to inclusive talent.',
    },
    {
      icon: 'accessibility',
      title: 'Accessibility settings',
      description: 'High contrast, larger text, reduced motion, and keyboard-friendly navigation built in.',
    },
  ];

  readonly audiences: string[] = [
    'Professionals with disabilities building world-class careers',
    'Employers committed to inclusive, diverse hiring',
    'Recruiters discovering highly-qualified accessible talent',
    'Organizations growing diverse, high-performing teams',
  ];

  readonly pillars: Pillar[] = [
    {
      icon: 'user', iconBg: 'bg-primary-50', iconColor: 'text-primary',
      titleColor: 'text-primary', title: 'Profiles',
      desc: 'Skills, experience, education, and accessibility preferences all in one place.',
    },
    {
      icon: 'briefcase', iconBg: 'bg-secondary-50', iconColor: 'text-secondary',
      titleColor: 'text-secondary', title: 'Jobs',
      desc: 'Search roles, apply online, and track applications effortlessly.',
    },
    {
      icon: 'network', iconBg: 'bg-accent-50', iconColor: 'text-accent',
      titleColor: 'text-accent', title: 'Network',
      desc: 'Discover professionals, send requests, and grow your circle.',
    },
    {
      icon: 'accessibility', iconBg: 'bg-primary-50', iconColor: 'text-primary',
      titleColor: 'text-primary', title: 'Access',
      desc: 'High contrast, large text, keyboard nav, and screen-reader-friendly UI.',
    },
  ];

  readonly accessFeatures: AccessFeature[] = [
    { icon: 'accessibility', label: 'Screen Reader Support' },
    { icon: 'check',         label: 'Keyboard Navigation' },
    { icon: 'settings',      label: 'High Contrast Mode' },
    { icon: 'user',          label: 'Large Text Option' },
    { icon: 'bell',          label: 'Focus Indicators' },
    { icon: 'check',         label: 'Reduced Motion' },
  ];
}
