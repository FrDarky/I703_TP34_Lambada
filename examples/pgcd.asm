DATA SEGMENT
	a DD
	b DD
	aux DD
DATA ENDS
CODE SEGMENT
	in eax
	mov a, eax
	push eax
	in eax
	mov b, eax
	push eax
debut_while_1:
	mov ebx, b
	mov eax, 0
	sub eax, ebx
	jl vrai_lt_1
	mov eax, 0
	jmp sortie_lt_1
vrai_lt_1:
	mov eax, 1
sortie_lt_1:
	jz sortie_while_1
	mov ebx, b
	mov eax, a
	mov ecx, eax
	div ecx, ebx
	mul ecx, ebx
	sub eax, ecx
	push eax
	pop eax
	mov aux, eax
	push eax
	mov eax, b
	mov a, eax
	push eax
	mov eax, aux
	mov b, eax
	push eax
	jmp debut_while_1
sortie_while_1:
	mov eax, a
	out eax
CODE ENDS
