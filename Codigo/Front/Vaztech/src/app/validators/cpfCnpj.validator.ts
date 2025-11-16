import { Directive } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

@Directive({
  selector: '[appCpfCnpjValidator]',
  providers: [{ provide: NG_VALIDATORS, useExisting: CpfCnpjValidator, multi: true }],
})
export class CpfCnpjValidator implements Validator {
  validate(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (value === null || value === undefined) {
      return null; // Don't validate if the value is empty
    }
    return value.length === 14 || value.length === 18 ? null : { cpfCnpjInvalido: true };
  }
}
