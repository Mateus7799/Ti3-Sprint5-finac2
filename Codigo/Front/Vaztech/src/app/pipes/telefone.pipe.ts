import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'telefone',
})
export class TelefonePipe implements PipeTransform {
  transform(value: string): string {
    if (value.length === 11)
      return `(${value.slice(0, 2)}) ${value[2]}${value.slice(3, 7)}-${value.slice(7, 11)}`;
    return 'ERRO';
  }
}
