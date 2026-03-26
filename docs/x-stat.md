# x-stat

Metric display component for presenting a label, value, and contextual information.

## Attributes

variant: default | subtle | positive | warning | danger  
align: start | center | end  
size: sm | md | lg  
emphasis: normal | high  
trend: up | down | neutral  
loading: boolean  
label: string  
value: string  
hint: string

## Slots

icon  
label  
value  
hint  
default

## Accessibility

role="group"  
aria-busy when loading  
aria-label derived from label

## Styling

Uses CSS custom properties and supports theme overrides.
