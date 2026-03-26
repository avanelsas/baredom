# x-card

Themeable surface container for grouping related content.

## Attributes

variant: elevated | outlined | filled | ghost  
padding: none | sm | md | lg  
radius: none | sm | md | lg | xl  
interactive: boolean  
disabled: boolean  
label: string

## Properties

interactive : boolean  
disabled : boolean

## Event

press

## Accessibility

Default role: group

Interactive mode:
role=button
Enter / Space activate press

Disabled prevents interaction.

## Slot

Default slot only.

## Styling

Use CSS variables:

--x-card-background  
--x-card-color  
--x-card-border-color  
--x-card-shadow  
--x-card-focus-ring  

Override them on host.

## Example

```html
<x-card variant="outlined" padding="md">
  Content here
</x-card>
